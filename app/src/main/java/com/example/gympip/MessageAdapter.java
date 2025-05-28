package com.example.gympip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter pentru afișarea mesajelor într-un RecyclerView.
 * Leagă obiectele de tip {@link Message} de layout-ul XML al fiecărui mesaj.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messages;

    /**
     * ViewHolder-ul care conține referințele la elementele UI din fiecare item de mesaj.
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView senderName;
        TextView timestamp;

        /**
         * Constructor ce leagă elementele din XML de variabilele Java.
         *
         * @param itemView View-ul individual al fiecărui mesaj.
         */
        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            senderName = itemView.findViewById(R.id.message_sender);
            timestamp = itemView.findViewById(R.id.message_time);
        }
    }

    /**
     * Constructorul adapterului.
     *
     * @param messages Lista de mesaje care vor fi afișate.
     */
    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * Creează un nou ViewHolder când este nevoie de un nou element în listă.
     *
     * @param parent   Părintele în care va fi adăugat noul view.
     * @param viewType Tipul view-ului (nerelevant în acest caz).
     * @return Un nou {@link MessageViewHolder}.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mess_layout, parent, false);
        return new MessageViewHolder(view);
    }

    /**
     * Leagă datele dintr-un obiect {@link Message} de un ViewHolder.
     *
     * @param holder   ViewHolder-ul în care se vor afișa datele.
     * @param position Poziția mesajului în listă.
     */
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getText());
        holder.senderName.setText(message.getSenderId());

        Format f = new Format();
        if (!message.getTimestamp().isEmpty()) {
            holder.timestamp.setText(f.formatTime(message.getTimestamp()));
        }
    }

    /**
     * Returnează numărul total de mesaje din listă.
     *
     * @return Numărul de mesaje.
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }
}
