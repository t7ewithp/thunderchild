package ml.withp.model;
import java.util.Date;

public class Tweet {
    private final String author;
    private final String data;
    private final Date day;
    public Tweet(String author, String data, Date day) {
        this.author = author;
        this.data = data;
        this.day = day;
    }

    public Date getDay() {
        return day;
    }

    public String getData() {
        return data;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return author + "|" + day.toString() + "|" + data;
    }

    @Override
    public int hashCode() {
        return author.hashCode() * data.hashCode() * day.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(this.getClass() != o.getClass()) return false;
        Tweet t = (Tweet) o;
        return t.getAuthor().equals(this.author)
                && t.getDay().equals(this.day)
                && t.getData().equals(this.data);
    }
}
