/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.chat.common;
import java.time.LocalDateTime;
import java.io.Serializable;
/**
 *
 * @author panos
 */
public class Message implements Serializable{
    private String username;
    private String data;
    private LocalDateTime timestmp;
    private String dataType;
    
    public Message(String username, String message, LocalDateTime timestmp, String msgType){
        this.username = username;
        this.data = message;
        this.timestmp = timestmp;
        this.dataType = msgType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(LocalDateTime timestmp) {
        this.timestmp = timestmp;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    @Override
    public String toString(){
        return this.username+" : "+this.data;
    }
}
