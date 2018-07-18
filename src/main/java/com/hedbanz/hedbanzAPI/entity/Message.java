package com.hedbanz.hedbanzAPI.entity;

import com.hedbanz.hedbanzAPI.constant.MessageType;

import javax.persistence.*;
import java.sql.Timestamp;

import static javax.persistence.FetchType.EAGER;

@Entity(name = "Message")
@Table(name = "message")
public class Message implements Cloneable {
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

    @Column(name = "create_date")
    private Timestamp createDate;

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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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

        if (id != null ? !id.equals(message.id) : message.id != null) return false;
        if (senderUser != null ? !senderUser.equals(message.senderUser) : message.senderUser != null) return false;
        if (text != null ? !text.equals(message.text) : message.text != null) return false;
        if (type != null ? !type.equals(message.type) : message.type != null) return false;
        return createDate != null ? createDate.equals(message.createDate) : message.createDate == null;
    }

    @Override
    public int hashCode() {
        int result = senderUser != null ? senderUser.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
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
                .build();
        return Message.Builder().setText(text)
                .setQuestion(question)
                .setRoom(room)
                .setSenderUser(senderUser)
                .setType(type)
                .setCreateDate(createDate)
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
            Message.this.setCreateDate(createDate);
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
