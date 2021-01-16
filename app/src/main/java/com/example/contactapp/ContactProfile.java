package com.example.contactapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ContactProfile extends AppCompatActivity {

    ListView profile_list_view;
    ArrayList<String> amigos = new ArrayList<>();
    profileAdaptor adaporrr;
    CopyOnWriteArrayList<contact_info> contact_info_profile = new CopyOnWriteArrayList<>();


    public int find_index_contact_info(String nombre_de_amigo){
        for(int i = 0; i< this.contact_info_profile.size(); i++){
            if(this.contact_info_profile.get(i).contact_name.equals(nombre_de_amigo)){
                return i;
            }
            else
                continue;
        }
        return 0;//if not found
    }

    public CopyOnWriteArrayList<contact_info> json_decoder_infos(String json_str){
        CopyOnWriteArrayList<contact_info> relationship_list = new CopyOnWriteArrayList<>();
        try {
            JSONArray json_arr = new JSONArray(json_str);
            for (int i=0; i<json_arr.length(); i++){
                JSONObject json_obj = json_arr.getJSONObject(i);
                String contact_name = json_obj.getString("name1");
                String contact_phone = json_obj.getString("phone1");
                String friends = json_obj.getString("friends1");
                MainActivity obj = new MainActivity();
                ArrayList<String> friend_of_this = obj.str_to_list(friends);
                relationship_list.add(new contact_info(contact_name, contact_phone,friend_of_this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return relationship_list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("This is the contact profile");
        setContentView(R.layout.profile_layout);

        TextView title_name = findViewById(R.id.txtview_name_profile);
        TextView title_phone = findViewById(R.id.txtview_phoneNum_profile);
        TextView title_relate = findViewById(R.id.txt_relationship_profile);
        title_name.setTextColor(Color.BLUE);
        title_phone.setTextColor(Color.BLUE);
        title_relate.setTextColor(Color.BLUE);

        Intent here_profile = getIntent();
        String name = here_profile.getStringExtra("lookup_name");
        String phone = here_profile.getStringExtra("lookup_phone");
        String related_people = here_profile.getStringExtra("look_up_relates");
        MainActivity object = new MainActivity();
        amigos = object.str_to_list(related_people);
        String all_infos = here_profile.getStringExtra("json_info");
        contact_info_profile = json_decoder_infos(all_infos);

        TextView txtview_name = findViewById(R.id.name_forShow);
        txtview_name.setText(name);
        TextView txtview_phone = findViewById(R.id.phone_forShow);
        txtview_phone.setText(phone);

        txtview_name. setTextIsSelectable(true);
        txtview_phone. setTextIsSelectable(true);

        profile_list_view = findViewById(R.id.list_relatioship_profile);
        adaporrr = new profileAdaptor(this, this.amigos);
        profile_list_view.setAdapter(adaporrr);
        adaporrr.notifyDataSetChanged();
        profile_list_view.deferNotifyDataSetChanged();

        profile_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(related_people.equals(""))
                {
                    Intent intent= getIntent();
                    finish();
                    startActivity(intent);
                } else{
                    Intent to_itself = new Intent(ContactProfile.this, ContactProfile.class);
                    int index = find_index_contact_info(amigos.get(position));
                    to_itself.putExtra("lookup_name", contact_info_profile.get(index).contact_name);
                    to_itself.putExtra("lookup_phone",contact_info_profile.get(index).phone_number);
                    MainActivity m = new MainActivity();
                    String condensed_friend = m.list_to_str(contact_info_profile.get(index).friend_list);

                    to_itself.putExtra("look_up_relates", condensed_friend);
                    String json_info = m.jsonEncoder_info(contact_info_profile);
                    to_itself.putExtra("json_info",json_info);
                    startActivity(to_itself);
                }
            }
        });
    }

    class profileAdaptor extends BaseAdapter{
        Context context;
        ArrayList<String> friends_list;
        public profileAdaptor(Context context, ArrayList<String> friends_list){
            this.context = context;
            this.friends_list = friends_list;
        }
        @Override
        public int getCount() { return this.friends_list.size(); }
        @Override
        public Object getItem(int position) { return null; }
        @Override
        public long getItemId(int position) { return 0; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(this.context, R.layout.list_names_only, null);
            TextView each_friend = (TextView)convertView.findViewById(R.id.each_friend);
            each_friend.setText(this.friends_list.get(position));
            return convertView;
        }
    }

}
