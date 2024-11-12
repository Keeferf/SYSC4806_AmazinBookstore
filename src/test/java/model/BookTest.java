package model;

import com.bookstore.model.Book;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookTest {

    private Book book;

    @Before
    public void setUp() {
        book = new Book("Test ISBN", "Test Title", "Test Description", "Test Author",
                "Test Publisher", "http://example.com/picture.jpg",
                29.99, 10);
    }

    @Test
    public void getIsbn() {
        book.setIsbn("123456789L");
        assertEquals("123456789L", book.getIsbn());
    }

    @Test
    public void setIsbn() {
        book.setIsbn("987654321L");
        assertEquals("987654321L", book.getIsbn());
    }

    @Test
    public void getTitle() {
        assertEquals("Test Title", book.getTitle());
    }

    @Test
    public void setTitle() {
        book.setTitle("New Title");
        assertEquals("New Title", book.getTitle());
    }

    @Test
    public void getDescription() {
        assertEquals("Test Description", book.getDescription());
    }

    @Test
    public void setDescription() {
        book.setDescription("New Description");
        assertEquals("New Description", book.getDescription());
    }

    @Test
    public void getAuthor() {
        assertEquals("Test Author", book.getAuthor());
    }

    @Test
    public void setAuthor() {
        book.setAuthor("New Author");
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    public void getPublisher() {
        assertEquals("Test Publisher", book.getPublisher());
    }

    @Test
    public void setPublisher() {
        book.setPublisher("New Publisher");
        assertEquals("New Publisher", book.getPublisher());
    }

    @Test
    public void getPictureURL() {
        assertEquals("http://example.com/picture.jpg", book.getPictureURL());
    }

    @Test
    public void setPictureURL() {
        book.setPictureURL("http://example.com/newpicture.jpg");
        assertEquals("http://example.com/newpicture.jpg", book.getPictureURL());
    }

    @Test
    public void getPrice() {
        assertEquals(29.99, book.getPrice(), 0.001);
    }

    @Test
    public void setPrice() {
        book.setPrice(39.99);
        assertEquals(39.99, book.getPrice(), 0.001);
    }

    @Test
    public void getInventory() {
        assertEquals(10, book.getInventory().intValue());
    }

    @Test
    public void setInventory() {
        book.setInventory(20);
        assertEquals(20, book.getInventory().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNegativePrice() {
        book.setPrice(-10.00);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNegativeInventory() {
        book.setInventory(-5);
    }

}
