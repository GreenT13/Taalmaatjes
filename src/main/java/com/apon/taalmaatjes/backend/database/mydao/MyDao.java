package com.apon.taalmaatjes.backend.database.mydao;

public interface MyDao<E> {
    boolean generateIds(E pojo);
    Integer getMaxId();
    String getMaxExtId();
    //E getPojo(String externalIdentifier);
    boolean insertPojo(E pojo);
    boolean updatePojo(E pojo);
    //boolean deletePojo(String externalIdentifier);
}
