package com.sga.almacenes.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sga.almacenes.R;
import com.sga.almacenes.data.local.Movimiento;

import java.util.ArrayList;
import java.util.List;

public class MovimientoAdapter
        extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private List<Movimiento> movimientos = new ArrayList<>();

    public void actualizarLista(List<Movimiento> nuevaLista) {
        movimientos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View vista = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_movimiento, parent, false);

        return new MovimientoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MovimientoViewHolder holder,
            int position
    ) {
        Movimiento movimiento = movimientos.get(position);

        holder.tvTipo.setText(movimiento.getTipo());
        holder.tvProducto.setText(movimiento.getNombreProducto());
        holder.tvCodigo.setText(
                "Código: " + movimiento.getCodigoProducto()
        );

        int colorTexto;
        int colorFondo;
        String textoCantidad;

        switch (movimiento.getTipo()) {
            case "ENTRADA":
                colorTexto = 0xFF2E7D32;
                colorFondo = 0xFFE8F5E9;
                textoCantidad =
                        "Cantidad: +" +
                                movimiento.getCantidad() +
                                " unidades";
                break;

            case "SALIDA":
                colorTexto = 0xFFC62828;
                colorFondo = 0xFFFFEBEE;
                textoCantidad =
                        "Cantidad: -" +
                                movimiento.getCantidad() +
                                " unidades";
                break;

            case "AJUSTE":
                colorTexto = 0xFF6A1B9A;
                colorFondo = 0xFFF3E5F5;

                int diferencia =
                        movimiento.getStockNuevo() -
                                movimiento.getStockAnterior();

                String signo = diferencia > 0 ? "+" : "";

                textoCantidad =
                        "Diferencia: " + signo +
                                diferencia + " unidades";
                break;

            default:
                colorTexto = 0xFF455A64;
                colorFondo = 0xFFECEFF1;
                textoCantidad =
                        "Cantidad: " +
                                movimiento.getCantidad();
                break;
        }

        holder.tvTipo.setTextColor(colorTexto);
        holder.tvTipo.setBackgroundTintList(
                ColorStateList.valueOf(colorFondo)
        );

        holder.tvCantidad.setTextColor(colorTexto);
        holder.tvCantidad.setText(textoCantidad);

        holder.tvStock.setText(
                "Stock: " + movimiento.getStockAnterior() +
                        " → " + movimiento.getStockNuevo()
        );

        holder.tvUsuario.setText(
                "Registrado por: " + movimiento.getUsuario()
        );

        holder.tvFecha.setText(movimiento.getFechaHora());

        if (movimiento.isSincronizado()) {
            holder.tvSincronizacion.setText("Sincronizado");
            holder.tvSincronizacion.setTextColor(0xFF2E7D32);
        } else {
            holder.tvSincronizacion.setText(
                    "Pendiente de sincronización"
            );
            holder.tvSincronizacion.setTextColor(0xFFE65100);
        }
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }

    static class MovimientoViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTipo;
        TextView tvProducto;
        TextView tvCodigo;
        TextView tvCantidad;
        TextView tvStock;
        TextView tvUsuario;
        TextView tvFecha;
        TextView tvSincronizacion;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTipo = itemView.findViewById(
                    R.id.tvMovimientoTipo
            );
            tvProducto = itemView.findViewById(
                    R.id.tvMovimientoProducto
            );
            tvCodigo = itemView.findViewById(
                    R.id.tvMovimientoCodigo
            );
            tvCantidad = itemView.findViewById(
                    R.id.tvMovimientoCantidad
            );
            tvStock = itemView.findViewById(
                    R.id.tvMovimientoStock
            );
            tvUsuario = itemView.findViewById(
                    R.id.tvMovimientoUsuario
            );
            tvFecha = itemView.findViewById(
                    R.id.tvMovimientoFecha
            );
            tvSincronizacion = itemView.findViewById(
                    R.id.tvMovimientoSincronizacion
            );
        }
    }
}