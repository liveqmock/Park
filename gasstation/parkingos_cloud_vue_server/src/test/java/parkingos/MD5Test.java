package parkingos;

import parkingos.com.bolink.utils.StringUtils;

import java.util.*;

public class MD5Test {

    public static void main(String args[]){
        String str = "";
        ArrayList<String> list = new ArrayList<>();
        list.add( "parkedID" );
        list.add( "deviceID" );
        list.add( "Info" );
        list.add( "Status" );
        Collections.sort(list);

        Map<String,Object> map = new HashMap<>(  );
        map.put( "parkedID","30001" );
        map.put( "deviceID","1002" );
        map.put( "Info","欢迎下次再来" );
        map.put( "Status",1 );

        StringBuilder sb = new StringBuilder();
        int i=0;
        for(String key:list){
            Object value= map.get( key );
            if(value!=null && !value.equals( "" )){
                //sb.append(key+"="+value+"&");
                sb.append(key+"="+value);
            }
            if(i!=list.size()-1){
                sb.append( "&" );
            }
            i++;
        }
        sb.append( "key=digisterGas" );

        String sign = StringUtils.MD5( sb.toString() ).toUpperCase();
        System.out.println( "排序后字符串:"+sb.toString() );
        System.out.println("排序后MD5结果:"+ sign );
    }


    public void md5Test(){

    }
}
