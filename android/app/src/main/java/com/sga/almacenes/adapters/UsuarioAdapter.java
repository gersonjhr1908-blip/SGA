package com.sga.almacenes.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sga.almacenes.R;
import com.sga.almacenes.data.local.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter
        extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> usuarios = new ArrayList<>();
    private final OnCambiarEstadoListener listener;

    public interface OnCambiarEstadoListener {
        void onCambiarEstado(Usuario usuario);
    }

    public UsuarioAdapter(OnCambiarEstadoListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<Usuario> nuevaLista) {
        usuarios = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View vista = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);

        return new UsuarioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UsuarioViewHolder holder,
            int position
    ) {
        Usuario usuario = usuarios.get(position);

        holder.tvNombreCompleto.setText(
                usuario.getNombreCompleto()
        );

        holder.tvNombreUsuario.setText(
                "Usuario: " + usuario.getNombreUsuario()
        );

        holder.tvRol.setText(usuario.getRol());

        if ("ADMINISTRADOR".equals(usuario.getRol())) {
            holder.tvRol.setTextColor(0xFF6A1B9A);
            holder.tvRol.setBackgroundTintList(
                    ColorStateList.valueOf(0xFFF3E5F5)
            );
        } else {
            holder.tvRol.setTextColor(0xFF1565C0);
            holder.tvRol.setBackgroundTintList(
                    ColorStateList.valueOf(0xFFE3F2FD)
            );
        }

        if (usuario.isActivo()) {
            holder.tvEstado.setText("Estado: ACTIVO");
            holder.tvEstado.setTextColor(0xFF2E7D32);

            holder.btnCambiarEstado.setText("DESACTIVAR");
            holder.btnCambiarEstado.setBackgroundTintList(
                    ColorStateList.valueOf(0xFFC62828)
            );
        } else {
            holder.tvEstado.setText("Estado: INACTIVO");
            holder.tvEstado.setTextColor(0xFFC62828);

            holder.btnCambiarEstado.setText("ACTIVAR");
            holder.btnCambiarEstado.setBackgroundTintList(
                    ColorStateList.valueOf(0xFF2E7D32)
            );
        }

        if (usuario.isSincronizado()) {
            holder.tvSincronizacion.setText("Sincronizado");
            holder.tvSincronizacion.setTextColor(0xFF2E7D32);
        } else {
            holder.tvSincronizacion.setText(
                    "Pendiente de sincronización"
            );
            holder.tvSincronizacion.setTextColor(0xFFE65100);
        }

        holder.btnCambiarEstado.setOnClickListener(v ->
                listener.onCambiarEstado(usuario)
        );
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class UsuarioViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNombreCompleto;
        TextView tvNombreUsuario;
        TextView tvRol;
        TextView tvEstado;
        TextView tvSincronizacion;
        Button btnCambiarEstado;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreCompleto = itemView.findViewById(
                    R.id.tvUsuarioNombreCompleto
            );

            tvNombreUsuario = itemView.findViewById(
                    R.id.tvUsuarioNombre
            );

            tvRol = itemView.findViewById(
                    R.id.tvUsuarioRol
            );

            tvEstado = itemView.findViewById(
                    R.id.tvUsuarioEstado
            );

            tvSincronizacion = itemView.findViewById(
                    R.id.tvUsuarioSincronizacion
            );

            btnCambiarEstado = itemView.findViewById(
                    R.id.btnCambiarEstadoUsuario
            );
        }
    }
}