package ml.withp.model;

public class YTComment {
    private final String author;
    private final String data;
    public YTComment(String a, String d) {
        author = a;
        data = d;
    }

    public String getAuthor() {
        return author;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return author + ": " + data;
    }

    @Override
    public int hashCode() {
        return author.hashCode() * data.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(this.getClass() != o.getClass()) return false;
        YTComment y = (YTComment) o;
        return y.getAuthor().equals(this.author)
                && y.getData().equals(this.data);
    }
}
