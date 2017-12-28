package movies.test.softserve.movies.db.entity;

/**
 * Created by root on 28.12.17.
 */

public class MovieFirebaseDTO {
    private Integer id;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
