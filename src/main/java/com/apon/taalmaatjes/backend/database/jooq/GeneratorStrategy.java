package com.apon.taalmaatjes.backend.database.jooq;

import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;

public class GeneratorStrategy extends DefaultGeneratorStrategy {

    /**
     * All Pojo filenames should end with Pojo.
     * @param definition
     * @param mode
     * @return
     */
    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        switch (mode){
            case POJO:
                return super.getJavaClassName(definition, mode) + "Pojo";
//            Apparently DAO's don't work :(
//            case DAO:
//                return super.getJavaClassName(definition, mode) + "Generated";
            default:
                return super.getJavaClassName(definition, mode);
        }
    }
}
