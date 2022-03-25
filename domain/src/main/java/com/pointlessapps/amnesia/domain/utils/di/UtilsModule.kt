package com.pointlessapps.amnesia.domain.utils.di

import com.pointlessapps.amnesia.domain.utils.DATE_FORMAT
import com.pointlessapps.amnesia.domain.utils.DateFormatter
import org.koin.dsl.module

internal val utilsModule = module {
    single {
        DateFormatter(
            dateFormat = DATE_FORMAT,
        )
    }
}
