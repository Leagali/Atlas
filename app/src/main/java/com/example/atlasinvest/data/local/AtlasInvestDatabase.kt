package com.example.atlasinvest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.atlasinvest.data.local.dao.AtivoDao
import com.example.atlasinvest.data.local.dao.CategoriaDao
import com.example.atlasinvest.data.local.dao.CotacaoDao
import com.example.atlasinvest.data.local.dao.DespesaFixaDao
import com.example.atlasinvest.data.local.dao.MetaDao
import com.example.atlasinvest.data.local.dao.MovimentacaoDao
import com.example.atlasinvest.data.local.dao.UsuarioDao
import com.example.atlasinvest.data.local.entity.Ativo
import com.example.atlasinvest.data.local.entity.Categoria
import com.example.atlasinvest.data.local.entity.Cotacao
import com.example.atlasinvest.data.local.entity.DespesaFixa
import com.example.atlasinvest.data.local.entity.Meta
import com.example.atlasinvest.data.local.entity.Movimentacao
import com.example.atlasinvest.data.local.entity.Usuario

@Database(
        entities = [
Usuario::class,
Categoria::class,
Movimentacao::class,
Meta::class,
DespesaFixa::class,
Ativo::class,
Cotacao::class
    ],
version = 1,
exportSchema = false
        )
@TypeConverters(Converters::class)
abstract class AtlasInvestDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun movimentacaoDao(): MovimentacaoDao
    abstract fun metaDao(): MetaDao
    abstract fun despesaFixaDao(): DespesaFixaDao
    abstract fun ativoDao(): AtivoDao
    abstract fun cotacaoDao(): CotacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AtlasInvestDatabase? = null

        fun getInstance(context: Context): AtlasInvestDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                        context.applicationContext,
                        AtlasInvestDatabase::class.java,
                        "atlas_invest.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}