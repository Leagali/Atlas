package com.example.atlasinvest.data.local

import androidx.room.TypeConverter
import com.example.atlasinvest.data.local.entity.*

class Converters {
    @TypeConverter
    fun fromTipoCategoria(value: TipoCategoria): String = value.name
    @TypeConverter
    fun toTipoCategoria(value: String): TipoCategoria = TipoCategoria.valueOf(value)

    @TypeConverter
    fun fromTipoMovimentacao(value: TipoMovimentacao): String = value.name
    @TypeConverter
    fun toTipoMovimentacao(value: String): TipoMovimentacao = TipoMovimentacao.valueOf(value)

    @TypeConverter
    fun fromStatusMeta(value: StatusMeta): String = value.name
    @TypeConverter
    fun toStatusMeta(value: String): StatusMeta = StatusMeta.valueOf(value)

    @TypeConverter
    fun fromPeriodicidade(value: Periodicidade): String = value.name
    @TypeConverter
    fun toPeriodicidade(value: String): Periodicidade = Periodicidade.valueOf(value)

    @TypeConverter
    fun fromTipoAtivo(value: TipoAtivo): String = value.name
    @TypeConverter
    fun toTipoAtivo(value: String): TipoAtivo = TipoAtivo.valueOf(value)
}