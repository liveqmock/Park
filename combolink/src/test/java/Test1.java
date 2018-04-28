import com.zld.common_dao.util.OrmUtil;
import parkingos.com.bolink.beans.OrderTb;

public class Test1 {
    public static void main(String args[]){
        OrderTb orderTb = new OrderTb();
        OrmUtil ormUtil=new OrmUtil();
        System.out.println(orderTb.getClass().getSimpleName());
        String tableName = ormUtil.getTableName(orderTb.getClass());
        System.out.println(tableName);
    }
}
