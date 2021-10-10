package tech.qvanphong.tllnbot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "image_command")
@Getter
@Setter
@NoArgsConstructor
public class ImageCommand {
    @Id
    private String commandName;

    private String content;

    private Long createdBy;

}
