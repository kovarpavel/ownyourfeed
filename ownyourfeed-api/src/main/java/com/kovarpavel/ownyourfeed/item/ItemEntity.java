package com.kovarpavel.ownyourfeed.item;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.kovarpavel.ownyourfeed.source.SourceEntity;

@Entity
@Table(schema = "rss_database", name = "item")
public class ItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String link;
    private String description;
    private String guid;
    private String author;
    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "source_id", referencedColumnName = "id")
    private SourceEntity source;

    public ItemEntity() { }

    public ItemEntity(
        final String title, 
        final String link, 
        final String description, 
        final String author,
        final LocalDateTime date,
        final String guid) {
            this.title = title;
            this.link = link;
            this.description = description;
            this.guid = guid;
            this.author = author;
            this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link +  '\'' + 
                ", guid='" + guid + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date.toString() + '\'' +
                '}';
    }

}
