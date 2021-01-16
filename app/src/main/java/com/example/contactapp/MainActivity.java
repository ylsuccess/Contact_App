package com.example.contactapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity{

    CopyOnWriteArrayList<contact_info> contact_infos = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<list_item_array> names_bool = new CopyOnWriteArrayList<list_item_array>();
    ListView listView;
    myAdaptor adp;

    public void initial_file(Context context, String filename)
    {
        File file_dir = context.getFilesDir();
        File file = new File(file_dir, filename + ".txt");

        try {
            if (file.exists()){
                Scanner scan = new Scanner(file);

                while (scan.hasNextLine()){
                    String line = scan.nextLine();
                    String[] words = line.split("&");
                    String name = words[0];
                    String phone = words[1];
                    ArrayList<String> friends_for_this = new ArrayList<>();
                    for(int i = 2; i< words.length; i++){
                        friends_for_this.add(words[i]);
                    }
                    contact_info new_info = new contact_info(name, phone, friends_for_this);
                    this.contact_infos.add(new_info);
                    list_item_array name_bool = new list_item_array(name, false);
                    this.names_bool.add(name_bool);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save_file(Context context, String filename, CopyOnWriteArrayList<contact_info> contact_infos){

        File file_dir = context.getFilesDir();
        File f = new File(file_dir, filename + ".txt");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            String contact_name = "";
            String contact_phone = "";
            ArrayList<String> friend;
            for (int counter = 0; counter < contact_infos.size(); counter++) {
                contact_name = contact_infos.get(counter).contact_name;
                contact_phone = contact_infos.get(counter).phone_number;
                friend = contact_infos.get(counter).friend_list;
                fos.write(contact_name.getBytes());
                fos.write("&".getBytes());
                fos.write(contact_phone.getBytes());
                fos.write("&".getBytes());
                for(int i = 0; i< friend.size(); i++){
                    if(i<friend.size()-1){
                        fos.write(friend.get(i).getBytes());
                        fos.write("&".getBytes());
                    }
                    else
                        fos.write(friend.get(i).getBytes());
                }
                fos.write("\r\n".getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean friend_exist(ArrayList<String> exist_friends, String target_friend){
        for(int i = 0; i< exist_friends.size(); i++){
            if(exist_friends.get(i).equals(target_friend)){
                return true;
            }
        }   return false;
    }

    public ArrayList<String> find_friend_list(String target){
        ArrayList<String> friends = new ArrayList<>();
        for(int i = 0; i< this.contact_infos.size(); i++){
            if(this.contact_infos.get(i).contact_name.equals(target)){
                friends = this.contact_infos.get(i).friend_list;
                break;
            }
        }   return friends;
    }

    public int index_of_each_friend(String this_friend){
        for(int i = 0; i< this.contact_infos.size(); i++){
            if(this.contact_infos.get(i).contact_name.equals(this_friend)){
                return i;
            }
        }   return 0;
    }

    public String jsonEncoder(CopyOnWriteArrayList<list_item_array> list){

        JSONArray tJsonArr = new JSONArray();
        for (list_item_array contactor: list) {
            JSONObject json_obj = new JSONObject();
            try {
                json_obj.put("name", contactor.contactName);
                json_obj.put("checked", contactor.checked);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tJsonArr.put(json_obj);
        }
        return tJsonArr.toString();
    }

    public String jsonEncoder_info(CopyOnWriteArrayList<contact_info> contact_infos){
        JSONArray tJsonArr = new JSONArray();
        for (contact_info contactor: contact_infos) {
            JSONObject json_obj = new JSONObject();
            try {
                json_obj.put("name1", contactor.contact_name);
                json_obj.put("phone1", contactor.phone_number);
                String friends = list_to_str(contactor.friend_list);
                json_obj.put("friends1",friends);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tJsonArr.put(json_obj);
        }
        return tJsonArr.toString();
    }

    public ArrayList<String> str_to_list(String str){

        String[] elements = str.split(" ");
        List<String> fixedLenghtList = Arrays.asList(elements);
        ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);
        return listOfString;
    }

    public String list_to_str(ArrayList<String> list){
        StringBuffer sb = new StringBuffer();
        for(String i: list){
            sb.append(i);
            sb.append(" "); }
        String str = sb.toString();
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("main activity on create");
        setContentView(R.layout.activity_main);

        Button add = findViewById(R.id.add_button);
        Button delete = findViewById(R.id.del_button);

        listView = findViewById(R.id.listview);
        initial_file(MainActivity.this, "all_contacts");
        adp = new myAdaptor(names_bool, this);
        listView.setAdapter(adp);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String people = jsonEncoder(names_bool);
                Intent to_contact_detail = new Intent(getApplicationContext(),ContactDetails.class);
                to_contact_detail.putExtra("people_list", people);
                startActivityForResult(to_contact_detail, 666);
            }
        });


        delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (adp.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "You do not have any contact set up yet.", Toast.LENGTH_LONG).show();
                }else{
                    int i = 0;
                    int j = 0;
                    for (list_item_array n : names_bool) {
                        if (n.checked) {
                            ArrayList<String> this_friend_list = find_friend_list(n.contactName);
                            for(int l = 0; l < this_friend_list.size(); l++){
                                int index_of_this_friend = index_of_each_friend(this_friend_list.get(l));
                                ArrayList<String> this_list = contact_infos.get(index_of_this_friend).friend_list;
                                for(int k= 0; k < this_list.size(); k++){
                                    if(this_list.get(k).equals(n.contactName))
                                    {
                                        this_list.remove(k);
                                    }
                                }
                            }
                            int ind = index_of_each_friend(n.contactName);
                            contact_infos.remove(ind);
                            names_bool.remove(n);
                            i++;
                        }
                        j++;
                    }
                    save_file(MainActivity.this, "all_contacts", contact_infos);
                    adp.notifyDataSetChanged();

                    if(i == 0){
                        Toast.makeText(MainActivity.this, "You have not selected an item to delete.", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(MainActivity.this, "Checked items were deleted", 1).show();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Click", Toast.LENGTH_SHORT).show();
                Intent toProfile = new Intent(getApplicationContext(), ContactProfile.class);
                toProfile.putExtra("lookup_name", contact_infos.get(position).contact_name);
                toProfile.putExtra("lookup_phone", contact_infos.get(position).phone_number);
                String friends_str = list_to_str(contact_infos.get(position).friend_list);
                toProfile.putExtra("look_up_relates", friends_str);
                toProfile.putExtra("json_info", jsonEncoder_info(contact_infos));
                startActivity(toProfile);
            }
        });

        listView.deferNotifyDataSetChanged();
        adp.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("main activity on start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("main activity on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("main activity on pause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 666) {
            if (resultCode == 666) {
                String new_name = data.getStringExtra("new_name");
                String new_phone = data.getStringExtra("new_phone");

                if(new_name.equals("") || new_phone.equals("")){
                    Toast.makeText(getApplicationContext(), "Unable to save: provide name " +
                            "and phone number",Toast.LENGTH_LONG).show();
                }else {
                    String new_relate = data.getStringExtra("new_related");
                    ArrayList<String> new_friends = str_to_list(new_relate);
                    boolean has_or_hasent_friend = data.getBooleanExtra("empty", false);

                    for (int j = 0; j < new_friends.size(); j++) {
                        for (int k = 0; k < contact_infos.size(); k++) {
                            if (new_friends.get(j).equals(contact_infos.get(k).contact_name)) {
                                boolean saved_friend_or_not = friend_exist(contact_infos.get(k).friend_list, new_name);
                                if (!saved_friend_or_not) {
                                    contact_infos.get(k).friend_list.add(new_name);
                                }
                            }
                        }
                    }

                    if (has_or_hasent_friend) {
                        ArrayList<String> empty_list = new ArrayList<>();
                        contact_info new_know = new contact_info(new_name, new_phone, empty_list);
                        this.contact_infos.add(new_know);
                        this.names_bool.add(new list_item_array(new_name, false));

                        save_file(MainActivity.this, "all_contacts", contact_infos);
                        listView.deferNotifyDataSetChanged();
                        adp.notifyDataSetChanged();
                    } else {
                        contact_info new_know = new contact_info(new_name, new_phone, new_friends);
                        this.contact_infos.add(new_know);
                        this.names_bool.add(new list_item_array(new_name, false));

                        save_file(MainActivity.this, "all_contacts", contact_infos);

                        listView.deferNotifyDataSetChanged();
                        adp.notifyDataSetChanged();
                    }
                    Toast.makeText(MainActivity.this, "Saved new contact", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No contact was saved", Toast.LENGTH_LONG).show();
            }
        }
    }
}

class contact_info{
    String contact_name;
    String phone_number;
    ArrayList<String> friend_list;
    public contact_info(String contact_name, String phone_number, ArrayList<String> friend_list){
        this.contact_name = contact_name;
        this.phone_number = phone_number;
        this.friend_list = friend_list;
    }
}

class list_item_array{
    String contactName;
    boolean checked;
    public list_item_array(String contactName,boolean checked){
        this.contactName = contactName;
        this.checked = checked;
    }
}

class myAdaptor extends BaseAdapter {
    CopyOnWriteArrayList<list_item_array> names_bool_list;
    Context context;

    public myAdaptor(CopyOnWriteArrayList<list_item_array> names_list, Context context) {
        this.names_bool_list = names_list;
        this.context = context;
    }

    public int getCount() {
        return this.names_bool_list.size();
    }

    public long getItemId(int position) {
        return 0;
    }

    public Object getItem(int i) {
        return 0;
    }

    //once getcount() returns the list size, getView() traverse each line of listview/arraylist(in this case) and perform
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.list_item, null);//load design layout of each listitem
        TextView contact_name = (TextView) convertView.findViewById(R.id.item_name);
        contact_name.setText(this.names_bool_list.get(position).contactName);
        CheckBox clicked = (CheckBox) convertView.findViewById(R.id.item_checkbox);
        clicked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myAdaptor.this.names_bool_list.get(position).checked = isChecked;
            }
        });
        return convertView;

    }
}