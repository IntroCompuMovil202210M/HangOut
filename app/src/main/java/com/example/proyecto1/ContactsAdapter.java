package com.example.proyecto1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactsAdapter extends CursorAdapter {

    private static final int CONTACT_ID_INDEX = 0;
    private static final int DISPLAY_NAME_INDEX = 1;

    public ContactsAdapter (Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_lista_contactos, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvIdContacto = (TextView) view.findViewById(R.id.idContacto);
        TextView tvNombre = (TextView) view.findViewById(R.id.nombreContacto);
        int idnum = cursor.getInt(CONTACT_ID_INDEX);
        String nombre = cursor.getString(DISPLAY_NAME_INDEX);
        ImageButton chat = (ImageButton) view.findViewById(R.id.chat_btn);

        tvIdContacto.setText(String.valueOf(idnum));
        tvNombre.setText(nombre);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
