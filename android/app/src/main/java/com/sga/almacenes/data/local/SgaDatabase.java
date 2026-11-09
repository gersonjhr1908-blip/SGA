package com.sga.almacenes.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Producto.class,
                Movimiento.class,
                Usuario.class
        },
        version = 3,
        exportSchema = false
)
public abstract class SgaDatabase extends RoomDatabase {

    public abstract ProductoDao productoDao();

    public abstract MovimientoDao movimientoDao();

    public abstract UsuarioDao usuarioDao();

    private static volatile SgaDatabase INSTANCIA;

    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    private static final Migration MIGRACION_1_2 =
            new Migration(1, 2) {
                @Override
                public void migrate(
                        @NonNull SupportSQLiteDatabase database
                ) {
                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS movimientos (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "productoId INTEGER NOT NULL, " +
                                    "codigoProducto TEXT, " +
                                    "nombreProducto TEXT, " +
                                    "tipo TEXT, " +
                                    "cantidad INTEGER NOT NULL, " +
                                    "stockAnterior INTEGER NOT NULL, " +
                                    "stockNuevo INTEGER NOT NULL, " +
                                    "usuario TEXT, " +
                                    "fechaHora TEXT, " +
                                    "sincronizado INTEGER NOT NULL)"
                    );
                }
            };

    private static final Migration MIGRACION_2_3 =
            new Migration(2, 3) {
                @Override
                public void migrate(
                        @NonNull SupportSQLiteDatabase database
                ) {
                    crearTablaUsuarios(database);
                    insertarUsuariosIniciales(database);
                }
            };

    private static final Callback CALLBACK_CREACION =
            new Callback() {
                @Override
                public void onCreate(
                        @NonNull SupportSQLiteDatabase database
                ) {
                    super.onCreate(database);
                    insertarUsuariosIniciales(database);
                }
            };

    private static void crearTablaUsuarios(
            SupportSQLiteDatabase database
    ) {
        database.execSQL(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "nombreCompleto TEXT, " +
                        "nombreUsuario TEXT, " +
                        "clave TEXT, " +
                        "rol TEXT, " +
                        "activo INTEGER NOT NULL, " +
                        "sincronizado INTEGER NOT NULL)"
        );

        database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS " +
                        "index_usuarios_nombreUsuario " +
                        "ON usuarios(nombreUsuario)"
        );
    }

    private static void insertarUsuariosIniciales(
            SupportSQLiteDatabase database
    ) {
        database.execSQL(
                "INSERT OR IGNORE INTO usuarios " +
                        "(nombreCompleto, nombreUsuario, clave, rol, " +
                        "activo, sincronizado) VALUES " +
                        "('Administrador SGA', 'admin', '123456', " +
                        "'ADMINISTRADOR', 1, 0)"
        );

        database.execSQL(
                "INSERT OR IGNORE INTO usuarios " +
                        "(nombreCompleto, nombreUsuario, clave, rol, " +
                        "activo, sincronizado) VALUES " +
                        "('Operario de almacén', 'operario', '123456', " +
                        "'OPERARIO', 1, 0)"
        );
    }

    public static SgaDatabase obtenerInstancia(Context context) {
        if (INSTANCIA == null) {
            synchronized (SgaDatabase.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    SgaDatabase.class,
                                    "sga_database"
                            )
                            .addMigrations(
                                    MIGRACION_1_2,
                                    MIGRACION_2_3
                            )
                            .addCallback(CALLBACK_CREACION)
                            .build();
                }
            }
        }

        return INSTANCIA;
    }
}