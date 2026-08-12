# CLAUDE.md

## JPA Entity 연관관계 설계 원칙

### 연관관계 편의 메서드 — `addBy` / `setBy`

양방향 연관관계는 항상 두 메서드로 동기화한다.

- FK를 보유한 쪽(자식, `@ManyToOne`) → `setBy(parent)`
- 컬렉션을 보유한 쪽(부모, `@OneToMany(mappedBy = ...)`) → `addBy(child)`

한쪽만 호출해도 반대편이 자동으로 맞춰지도록, 서로가 서로를 재귀 호출한다. 자기참조 트리(부모-자식이 같은 Entity)도 동일하게 적용한다.

```kotlin
class Bar : Audit() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fooId", updatable = false, nullable = false)
    var foo: Foo? = null
        protected set

    fun setBy(foo: Foo) {
        this.foo = foo
        if (!foo.bars.contains(this)) foo.addBy(this)
    }
}

class Foo : Audit() {
    @OneToMany(mappedBy = "foo", fetch = FetchType.LAZY)
    var bars: MutableList<Bar> = arrayListOf()
        protected set

    fun addBy(bar: Bar) {
        bars.add(bar)
        if (bar.foo != this) bar.setBy(this)
    }
}
```

- `id`, FK 참조 필드, 연관관계 컬렉션은 항상 `protected set`으로 캡슐화한다. 외부에서 필드에 직접 대입하지 말고 반드시 `setBy`/`addBy`를 통해서만 변경한다.

### `@ManyToOne` — `fetch = FetchType.LAZY`를 항상 명시한다

`@ManyToOne`의 JPA 기본값은 EAGER다. 명시하지 않으면 컬렉션을 조회할 때마다 연관 엔티티를 로딩하려는 추가 SELECT가 row 수만큼 발생해 N+1로 이어진다. **예외 없이 `fetch = FetchType.LAZY`를 붙인다.**

```kotlin
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "projectId")
var project: Project? = null
    protected set
```

조회 시점에 실제로 필요한 데이터는 LAZY 프록시에 의존하지 말고, `*DSLRepository`에서 fetch join으로 명시적으로 가져온다 (아래 QueryDSL 절 참고).

### `@OneToMany` — `mappedBy` + `LAZY` 필수, `cascade = [CascadeType.ALL]`로 하드 삭제를 명시적으로 전파한다

- FK는 항상 자식 쪽(`@ManyToOne`)이 소유한다. N:M 관계도 `@ManyToMany` 대신 별도 조인 Entity(아래 항목)로 명시적으로 분리하고, `@OneToMany(mappedBy = "...")`로 연결한다.
- `fetch = FetchType.LAZY`를 명시한다.
- 이 프로젝트는 soft delete를 쓰지 않고 `repository.delete()`/`deleteById()`로 실제 삭제한다 (soft delete는 "삭제 후 같은 자연키로 재등록" 시나리오가 UNIQUE 제약과 충돌하는 문제 때문에 도입하지 않기로 함 — 재등록 시 soft-deleted row는 애플리케이션 레벨 중복 체크는 통과하지만 DB의 UNIQUE 제약에는 그대로 걸린다). 따라서 `cascade = [CascadeType.ALL]`을 의도적으로 사용해, 부모 Entity에 `repository.delete()`가 호출되면 하위 Entity도 함께 DB에서 삭제되도록 한다. `HolidayCalendar.holidays`, `Topic.calendars`, `TopicCalendar.holidays`/`workdays`가 이 패턴이다.
- Flyway 마이그레이션(`V1__init_schema.sql`)의 FK 제약에도 동일하게 `ON DELETE CASCADE`가 걸려 있어, 애플리케이션 cascade와 DB 레벨 cascade가 함께 정합성을 보장한다.

```kotlin
@OneToMany(
    mappedBy = "foo",
    fetch = FetchType.LAZY,
    cascade = [CascadeType.ALL],
)
var bars: MutableList<Bar> = arrayListOf()
    protected set
```

### N:M — 조인 Entity로 명시적으로 분리한다

`@ManyToMany`는 사용하지 않는다. 대신 두 Entity를 각각 `@ManyToOne(fetch = LAZY)`로 참조하는 별도 Entity를 만든다.

- 양쪽에 `@JoinColumn(updatable = false, nullable = false)`를 붙인다 (연결은 생성 시 한 번만 맺고 변경하지 않음을 명시).
- 조인 Entity 자체가 연결/해제 이력을 표현하므로, 해제는 `status` 필드 + `expire()` 메서드로 soft delete한다 (조인 Entity를 지워도 양쪽 원본 Entity에는 영향이 없어 UNIQUE 제약 충돌 문제가 없다).
- `setBy(A)`, `setBy(B)` 두 개의 오버로드로 양쪽 부모와 각각 동기화한다.

이 패턴은 예시일 뿐, 현재 river-otter에는 N:M 관계나 이를 구현한 조인 Entity가 존재하지 않는다 (`calendar`/`topic`/`workingday`/`client` 도메인 어디에도 없음). 실제로 N:M 관계를 추가할 때 참고할 패턴으로 남겨둔다.

### 상태 필드(`status`) 캡슐화

위 N:M 조인 Entity처럼 soft delete를 쓰는 경우에 한해, `status` 필드는 `protected set`으로 캡슐화하고 값 변경은 반드시 `expire()` 같은 의도가 드러나는 Entity 메서드를 통해서만 한다. `var status = true`처럼 외부에서 직접 대입 가능한 형태로 두지 않는다. river-otter의 기존 Entity(`Holiday`, `Topic` 등)는 soft delete를 쓰지 않으므로 `status` 필드가 없다 — 위 `@OneToMany`/`cascade` 절 참고.

---

## JPA 연관관계의 기술적 한계와 설계 시 고려사항

Entity의 연관관계 구조는 조회 성능에 직접적인 영향을 준다. 새 연관관계를 설계하거나 QueryDSL 조회를 작성할 때 아래 한계를 반드시 고려한다.

### 1. N+1 — `@ManyToOne` EAGER 또는 LAZY 프록시 순회로 발생

- 원인: `@ManyToOne`을 EAGER로 두거나(위 규칙 위반), LAZY 프록시를 반복문 안에서 접근하는 경우 row마다 추가 SELECT가 나간다.
- 방지: `@ManyToOne`은 항상 LAZY로 선언하고, 실제로 필요한 시점(Finder/DSLRepository)에 fetch join으로 한 번에 가져온다. LAZY 프록시를 반복문에서 접근하지 않는다.

### 2. Fetch Join은 컬렉션(`@OneToMany`) 연관관계를 한 번에 하나까지만 지원한다

Hibernate는 한 쿼리에서 컬렉션(bag) fetch join을 2개 이상 동시에 수행할 수 없다(`MultipleBagFetchException`). 예를 들어 `User`는 `userAuthorityGrants`, `userPersonalGrants` 두 개의 `@OneToMany` 컬렉션을 갖는데, 이 둘을 한 쿼리에서 동시에 fetch join할 수 없다.

- **하나의 Entity가 컬렉션 연관관계를 2개 이상 가지도록 설계할 때는, 한 쿼리로 전부 fetch join할 수 없다는 것을 전제로 조회 전략을 같이 설계한다.**
- 대응: 컬렉션이 여러 개면 그중 하나만 fetch join하고 나머지는 `Set`으로 변경하거나(순서상 여러 `Set` fetch join도 카테시안 곱 문제는 남으므로 권장하지 않음), 컬렉션별로 쿼리를 분리해 ID 기준으로 batch 조회한다(`IN` 절 재조회 또는 `@BatchSize`).
- 즉 `ManyToOne`은 fetch join으로 한 번에, **컬렉션은 최대 1개까지만 fetch join, 나머지 컬렉션은 별도 쿼리로 분리**한다.

### 3. 컬렉션 Fetch Join + 페이징 병행 금지 (카테시안 곱)

컬렉션을 fetch join하면 부모 row가 자식 개수만큼 곱해져 반환된다. 이 상태에서 `offset`/`limit` 페이징을 걸면 Hibernate가 DB가 아닌 메모리에서 페이징을 수행해 전체 데이터를 로딩한다(경고 로그: `HHH000104`). **컬렉션 fetch join과 페이징은 같은 쿼리에서 함께 쓰지 않는다.** 페이징이 필요한 다건 조회는 `ManyToOne`만 fetch join하고, 컬렉션은 별도 쿼리로 분리한다 (이미 QueryDSL 절의 `findAllBy` 패턴을 따른다).

### 4. Entity를 API 응답으로 직접 직렬화하지 않는다

LAZY 연관관계가 있는 Entity를 Jackson으로 직접 직렬화하면 트랜잭션 밖에서 프록시에 접근해 `LazyInitializationException`이 발생한다. Controller는 항상 `*Resources.kt`의 DTO로 변환해 반환한다 (기존 규칙과 동일).

### 5. 양방향 참조 Entity에 `data class`/자동 생성 `toString`·`equals`를 쓰지 않는다

이 프로젝트의 Entity는 모두 plain `class`다. `data class`로 바꾸면 자동 생성되는 `toString()`/`equals()`/`hashCode()`가 양방향 연관관계를 서로 순회하며 `StackOverflowError`를 일으킬 수 있다. Entity는 계속 plain `class`로 유지한다.

## QueryDSL 활용

- QueryDSL을 사용하는 Repository 클래스는 `*DSLRepository`로 명명한다.
- 단건 조회: `ManyToOne` 연관관계는 모두 Join Fetch하여 한 번의 쿼리로 가져온다.
- 다건 조회: N+1이 발생하지 않도록 한다. `ManyToOne`은 fetch join으로 한 번에 가져오고, 컬렉션은 별도 쿼리로 분리한다.
- 단건 조회에서 하위 컬렉션이 있으면 Fetch Join을 사용하여 컬렉션을 한 번에 가져온다.

```kotlin
@Repository
class FooDSLRepository(private val queryFactory: JPAQueryFactory) {

    // 단건: ManyToOne + 컬렉션 모두 fetch join
    fun findWithRelations(id: Long): Foo? =
        queryFactory
            .selectFrom(foo)
            .join(foo.project, project).fetchJoin()
            .leftJoin(foo.items, item).fetchJoin()   // 하위 컬렉션 fetch join
            .where(foo.id.eq(id))
            .fetchOne()

    // 다건: ManyToOne fetch join, 컬렉션은 별도 쿼리로 N+1 방지
    fun findAllBy(projectId: Long): List<Foo> =
        queryFactory
            .selectFrom(foo)
            .join(foo.project, project).fetchJoin()
            .where(project.id.eq(projectId))
            .fetch()
}
```
