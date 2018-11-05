package com.example.android.bookventoria;

/**
 * {@link GenresList} Used to create GridView list of book genres
 * 3 fields: Genre name, Image Id, Genre code
 */
public class GenresList {

    // Genre Name
    private String genreName;

    // Genre Picture (Drawable resource Id)
    private int imageId;

    // Genre database Code
    private int genreCode;

    /**
     * Public Constructor: Create a new GenresList object using 3 parameters
     *
     * @param name Genre name
     * @param id   drawable reference id
     * @param code corresponding database code
     */
    public GenresList(String name, int id, int code) {
        genreName = name;
        imageId = id;
        genreCode = code;
    }

    /**
     * Method returns Genre Name
     *
     * @return String Genre Name
     */
    public String getGenreName() {
        return genreName;
    }

    /**
     * Sets Genre Name
     *
     * @param genreName String containing new genre name
     */
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    /**
     * Method returns Image Id
     *
     * @return int returned Image Id
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * Sets Image Id
     *
     * @param imageId int containing new image id
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    /**
     * Method returns corresponding database code
     *
     * @return int returned database code
     */
    public int getGenreCode() {
        return genreCode;
    }

    /**
     * Sets Genre Code
     *
     * @param genreCode int containing new genre code
     */
    public void setGenreCode(int genreCode) {
        this.genreCode = genreCode;
    }


}
