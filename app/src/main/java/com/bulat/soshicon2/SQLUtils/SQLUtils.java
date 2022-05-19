package com.bulat.soshicon2.SQLUtils;

//класс для упрощения работы с запросами SQL
public class SQLUtils {
    String[] DataArr;
    public SQLUtils(String... DataArr){
        this.DataArr = DataArr;
    }

    /*метод используется для формирования запроса регистрацционных данных в бд
    (name, password, email)*/

    public String InsertRegData(){
        String[] args = {"?name=", "&password=", "&email="};
        return createUrl(args);
    }
    public String Authorization(){
        String[] args = {"?login=", "&password="};
        return createUrl(args);
    }
    public String createUrl(String[] args){

        StringBuilder query = new StringBuilder();
        for (int i = 0; i < DataArr.length; i++){
            query.append(args[i]).append(DataArr[i]);
        }
        return query.toString();
    }
    public String input_event(){
        String[] args = {"?user_id=", "&content=","&nickname=", "&time=", "&latitude=", "&longitude="};
        return createUrl(args);
    }
}
