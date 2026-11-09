package com.sga.almacenes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sga.almacenes.R;
import com.sga.almacenes.data.local.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter
        extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos = new ArrayList<>();
    private final OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    public ProductoAdapter(OnProductoClickListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<Producto> nuevaLista) {
        productos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View vista = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);

        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductoViewHolder holder,
            int position
    ) {
        Producto producto = productos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvCodigo.setText("Código: " + producto.getCodigo());
        holder.tvStock.setText(
                "Stock: " + producto.getCantidad() + " unidades"
        );
        holder.tvUbicacion.setText(
                "Ubicación: " + producto.getUbicacion()
        );

        String lote = producto.getLote();

        if (lote == null || lote.isEmpty()) {
            holder.tvLote.setText("Lote: Sin registrar");
        } else {
            holder.tvLote.setText("Lote: " + lote);
        }

        if (producto.isSincronizado()) {
            holder.tvSincronizacion.setText("Sincronizado");
            holder.tvSincronizacion.setTextColor(0xFF2E7D32);
        } else {
            holder.tvSincronizacion.setText(
                    "Pendiente de sincronización"
            );
            holder.tvSincronizacion.setTextColor(0xFFE65100);
        }

        holder.itemView.setOnClickListener(v ->
                listener.onProductoClick(producto)
        );
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        TextView tvCodigo;
        TextView tvStock;
        TextView tvUbicacion;
        TextView tvLote;
        TextView tvSincronizacion;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvCodigo = itemView.findViewById(R.id.tvItemCodigo);
            tvStock = itemView.findViewById(R.id.tvItemStock);
            tvUbicacion = itemView.findViewById(R.id.tvItemUbicacion);
            tvLote = itemView.findViewById(R.id.tvItemLote);
            tvSincronizacion =
                    itemView.findViewById(R.id.tvItemSincronizacion);
        }
    }
}