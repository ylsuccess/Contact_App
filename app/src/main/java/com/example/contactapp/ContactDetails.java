package com.example.contactapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import java.lang.*;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ContactDetails extends AppCompatActivity {

    ArrayList<String> related_people = new ArrayList<>();
    myAdaptor ada;
    ListView listview2;
    CopyOnWriteArrayList<list_item_array> list;

    //decode contactName and checkBox status
    public CopyOnWriteArrayList<list_item_array> json_decoder(String json_str){
        CopyOnWriteArrayList<list_item_array> relationship_list = new CopyOnWriteArrayList<>();
        try {
            JSONArray json_arr = new JSONArray(json_str);
            for (int i=0; i<json_arr.length(); i++){
                JSONObject json_obj = json_arr.getJSONObject(i);
                String contact_name = json_obj.getString("name");
                Boolean checked = json_obj.getBoolean("checked");

                relationship_list.add(new list_item_array(contact_name, checked));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return relationship_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_contact_details);

        Intent intent = getIntent();
        String json_str = intent.getStringExtra("people_list");
        list = json_decoder(json_str);

        ada = new myAdaptor(list,getApplicationContext());
        listview2 = findViewById(R.id.list_relatioship);
        listview2.setAdapter(ada);

        EditText edit_name = findViewById(R.id.edit_name);
        EditText edit_phone = findViewById(R.id.edit_phone);
        String strValueName = edit_name.getText().toString();
        String strValuePhone = edit_phone.getText().toString();
        edit_name.setText(strValueName);
        edit_phone.setText(strValuePhone);
        StringBuffer sb = new StringBuffer();

        Button add_person = findViewById(R.id.button_Add_person);
        add_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_to_main = new Intent(ContactDetails.this, MainActivity.class);
                back_to_main.putExtra("new_name", edit_name.getText().toString());
                back_to_main.putExtra("new_phone", edit_phone.getText().toString());
                int count_check = 0;
                for (list_item_array n : list) {
                    if (n.checked) {
                        count_check ++;
                        related_people.add(n.contactName);
                    }
                }
                ada.notifyDataSetChanged();

                for (String s : related_people) {//convert ArrayList into a String
                    sb.append(s);
                    sb.append(" ");
                }
                String str = sb.toString();

                if(count_check== 0){
                    back_to_main.putExtra("empty",true);
                }else{
                    back_to_main.putExtra("empty",false);
                }

                back_to_main.putExtra("new_related", str);
                setResult(666, back_to_main);
                ada.notifyDataSetChanged();
                listview2.deferNotifyDataSetChanged();
                finish();
            }
        });
    }

}
