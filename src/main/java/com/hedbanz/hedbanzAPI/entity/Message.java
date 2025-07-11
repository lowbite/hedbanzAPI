package com.hedbanz.hedbanzAPI.entity;

import com.hedbanz.hedbanzAPI.constant.MessageType;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

import static jakarta.persistence.FetchType.EAGER;

@Entity(name = "Message")
@Table(name = "message")
public class Message extends AuditModel implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = EAGER)
    private User senderUser;

    @Column(name = "text")
    private String text;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @OneToOne(cascade = CascadeType.ALL, fetch = EAGER)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "room_id")
    private Room room;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Room getRoomMessage() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!Objects.equals(id, message.id)) return false;
        if (!Objects.equals(senderUser, message.senderUser)) return false;
        if (!Objects.equals(text, message.text)) return false;
        return !Objects.equals(type, message.type);
    }

    @Override
    public int hashCode() {
        int result = senderUser != null ? senderUser.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderUser=" + senderUser +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", question=" + question +
                ", room=" + room.getName() +
                '}';
    }

    @Override
    public Object clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Room room = new Room.Builder()
                .setId(this.room.getId())
                .setName(this.room.getName())
                .setIsPrivate(this.room.getIsPrivate())
                .setMaxPlayers(this.room.getMaxPlayers())
                .setCurrentPlayersNumber(this.room.getCurrentPlayersNumber())
                .setGameStatus(this.room.getGameStatus())
                .setPlayers(this.room.getPlayers())
                .setRoomAdmin(this.room.getRoomAdmin())
                .build();
        return Message.Builder().setText(text)
                .setQuestion(question)
                .setRoom(room)
                .setSenderUser(senderUser)
                .setType(type)
                .setCreateDate(new Timestamp(getCreatedAt().getTime()))
                .setId(id)
                .build();
    }

    public static Builder Builder() {
        return new Message().new Builder();
    }

    public class Builder {
        private Builder() {

        }

        public Builder setId(Long id) {
            Message.this.setId(id);
            return this;
        }

        public Builder setSenderUser(User senderUser) {
            Message.this.setSenderUser(senderUser);
            return this;
        }

        public Builder setText(String text) {
            Message.this.setText(text);
            return this;
        }

        public Builder setType(MessageType type) {
            Message.this.setType(type);
            return this;
        }

        public Builder setCreateDate(Timestamp createDate) {
            Message.this.setCreatedAt(createDate);
            return this;
        }

        public Builder setQuestion(Question question) {
            Message.this.setQuestion(question);
            return this;
        }

        public Builder setRoom(Room room) {
            Message.this.setRoom(room);
            return this;
        }

        public Message build() {
            return Message.this;
        }
    }
}
