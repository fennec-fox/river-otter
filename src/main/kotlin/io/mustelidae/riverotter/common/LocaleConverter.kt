package io.mustelidae.riverotter.common

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.Locale

@Converter(autoApply = true)
class LocaleConverter : AttributeConverter<Locale, String> {

    override fun convertToDatabaseColumn(attribute: Locale?): String? = attribute?.country

    override fun convertToEntityAttribute(dbData: String?): Locale? = dbData?.let { AvailableCountry.getLocale(it) }
}
