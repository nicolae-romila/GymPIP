package com.example.gympip;

/**
 * Clasa ce reprezintă un mesaj trimis într-un chat.
 * Conține textul mesajului, ID-ul expeditorului și un timestamp.
 */
public class Message {
    private String text;
    private String senderId;
    private String timestamp;

    /**
     * Constructor fără parametri necesar pentru Firebase.
     */
    public Message() {}

    /**
     * Constructor cu parametri pentru inițializarea completă a unui mesaj.
     *
     * @param text      Conținutul mesajului.
     * @param senderId  ID-ul utilizatorului care a trimis mesajul.
     * @param timestamp Timpul la care a fost trimis mesajul (în milisecunde ca string).
     */
    public Message(String text, String senderId, String timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    /**
     * Returnează textul mesajului.
     *
     * @return Textul mesajului.
     */
    public String getText() { return text; }

    /**
     * Returnează ID-ul expeditorului.
     *
     * @return ID-ul utilizatorului care a trimis mesajul.
     */
    public String getSenderId() { return senderId; }

    /**
     * Returnează timestamp-ul mesajului.
     *
     * @return Timpul la care a fost trimis mesajul (ca string).
     */
    public String getTimestamp() { return timestamp; }

    /**
     * Setează textul mesajului.
     *
     * @param text Noua valoare a textului.
     */
    public void setText(String text) { this.text = text; }

    /**
     * Setează ID-ul expeditorului.
     *
     * @param senderId Noua valoare a ID-ului expeditorului.
     */
    public void setSenderId(String senderId) { this.senderId = senderId; }

    /**
     * Setează timestamp-ul mesajului.
     *
     * @param timestamp Noua valoare a timestamp-ului.
     */
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
