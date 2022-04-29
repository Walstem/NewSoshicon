package com.bulat.soshicon2.checks;


import com.bulat.soshicon2.asynctasks.SendQuery;
import java.util.concurrent.ExecutionException;

public class UniqueNameCheck {
    public String uniqueness(String name) throws ExecutionException, InterruptedException {
         SendQuery request = new SendQuery("name_check.php");
         request.execute("?name="+name);
         return request.get();
    }
}
