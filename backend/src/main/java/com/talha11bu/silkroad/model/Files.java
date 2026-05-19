package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing a file uploaded to a session.
 *
 * <p>Each file is stored in Cloudflare R2 and referenced by its {@link #r2Key}.
 * The entity maintains a many-to-one relationship with the parent {@link Session},
 * and is cascade-deleted when the session is removed.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "A file stored within a session, backed by Cloudflare R2 storage")
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated primary key", example = "1")
    private int id;

    @Schema(description = "Storage key in R2 bucket", example = "SKR-7X9K2M/quarterly-report.pdf")
    private String r2Key;

    @Schema(description = "Original file name", example = "quarterly-report.pdf")
    private String fileName;

    @ToString.Exclude
    @JsonIgnoreProperties("files")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionID", nullable = false)
    @Schema(hidden = true)
    private Session session;

    /**
     * Creates a new file record linked to a session.
     *
     * @param fileName the user-facing original filename.
     * @param r2Key    the internal Cloudflare R2 object key.
     * @param session  the parent session this file belongs to.
     */
    public Files(String fileName, String r2Key, Session session) {
        this.fileName = fileName;
        this.r2Key = r2Key;
        this.session = session;
    }

}
