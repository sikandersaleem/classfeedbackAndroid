package com.example.classcomplain;

import java.util.ArrayList;

public class ComplainClass {

    public String catagories;
    public String childid;
    public String subject;
    public String complainlink;
    public String subjects;
    public String parentid;
    public String status;
    public String state;
    public String assignto;
    public String datetime;
    public ComplainClass()
    {

    }

    public  ComplainClass(String catagories,String childid,String subject, String complainlink,String subjects,String parentid,String status,String state,String assignto,String datetime)
    {
        this.catagories=catagories;
        this.childid=childid;
        this.subject=subject;
        this.complainlink=complainlink;
        this.subjects=subjects;
        this.parentid=parentid;
        this.status=status;
        this.state=state;
        this.assignto = assignto;
        this.datetime = datetime;
    }

}
