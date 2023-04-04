package com.kovarpavel.ownyourfeed.source;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kovarpavel.ownyourfeed.authentication.UserEntity;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(schema = "rss_database", name = "source")
public class SourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String url;
    private String description;
    @ManyToMany(
            mappedBy = "sources",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<UserEntity> users;

    public SourceEntity() {}

    public SourceEntity(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Source{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
