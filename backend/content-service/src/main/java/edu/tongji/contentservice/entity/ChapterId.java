package edu.tongji.contentservice.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Data
public class ChapterId implements Serializable {
    private Long novelId;
    private Long chapterId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChapterId chapterId1 = (ChapterId) o;
        return Objects.equals(novelId, chapterId1.novelId) && Objects.equals(chapterId, chapterId1.chapterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(novelId, chapterId);
    }
}