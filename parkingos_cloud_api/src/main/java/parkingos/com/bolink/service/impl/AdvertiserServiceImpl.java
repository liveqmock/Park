package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zld.common_dao.dao.CommonDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import parkingos.com.bolink.beans.AdvertisementTb;
import parkingos.com.bolink.beans.ComInfoTb;
import parkingos.com.bolink.beans.ParkAdvertiserTb;
import parkingos.com.bolink.service.AdvertiserService;
import parkingos.com.bolink.utlis.weixinpay.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdvertiserServiceImpl implements AdvertiserService {

    Logger logger = org.apache.log4j.Logger.getLogger( AdvertiserServiceImpl.class);

    @Autowired
    private CommonDao commonDao;

    @Override
    public JSONObject selectResultByConditions(Map<String, String> params) {
        String str = "{\"total\":0,\"page\":1,\"rows\":[]}";
        JSONObject result = JSONObject.parseObject(str);

        Long advId = Long.valueOf( params.get( "comid" ) );

        ParkAdvertiserTb parkAdvertiserTb = new ParkAdvertiserTb();
        parkAdvertiserTb.setAdvertiserId( advId );

        List<ParkAdvertiserTb> list = commonDao.selectListByConditions( parkAdvertiserTb );
        if(list!=null&&list.size()!=0){
            List<ComInfoTb> coms= new ArrayList<>();
            for(ParkAdvertiserTb obj:list){
                ComInfoTb comInfoTb = new ComInfoTb();
                comInfoTb.setId(obj.getParkId());

                comInfoTb = (ComInfoTb) commonDao.selectObjectByConditions( comInfoTb );
                if(comInfoTb!=null){
                    coms.add( comInfoTb );
                }
            }
            result.put( "total",coms.size() );
            result.put( "rows",coms );
            if(params.get("page")!=null){
                result.put("page",Integer.parseInt(params.get("page")));
            }
        }
        return result;
    }

    @Override
    public String create(HttpServletRequest req) {

        Long parkId = RequestUtil.getLong( req,"id",-1L  );
        Long advertiserId = RequestUtil.getLong( req,"comid",-1L );

        if(advertiserId==-1 ||parkId==-1 ){
            return "{\"state\":" + 0 + "}";
        }

        //检查停车场是否存在
        ComInfoTb comInfoTb = new ComInfoTb();
        comInfoTb.setId( parkId );
        int comCount = commonDao.selectCountByConditions( comInfoTb );
        if(comCount==0){
            return "{\"state\":" + 0 + "}";
        }

        ParkAdvertiserTb parkAdvertiserTb = new ParkAdvertiserTb();
        parkAdvertiserTb.setParkId( parkId );

        int count = commonDao.selectCountByConditions( parkAdvertiserTb );
        if(count>0){
            //该停车场已经绑定广告商  只需要修改绑定广告商
            ParkAdvertiserTb bean = (ParkAdvertiserTb) commonDao.selectObjectByConditions( parkAdvertiserTb );
            bean.setAdvertiserId( advertiserId );

            commonDao.updateByPrimaryKey( bean );

            return "{\"state\":" + 1 + "}";
        }

        //该停车场还没有绑定广告商  直接保存
        parkAdvertiserTb.setAdvertiserId( advertiserId );
        commonDao.insert( parkAdvertiserTb );

        return "{\"state\":" + 1 + "}";
    }

    @Override
    public String delete(HttpServletRequest request) {
        Long id = RequestUtil.getLong( request, "id", -1L );
        int delete = 0;
        if (id > 0) {
            ParkAdvertiserTb parkAdvertiserTb = new ParkAdvertiserTb();
            parkAdvertiserTb.setParkId( id );

            //查询绑定信息
            parkAdvertiserTb = (ParkAdvertiserTb) commonDao.selectObjectByConditions( parkAdvertiserTb );

            if(parkAdvertiserTb!=null){
                parkAdvertiserTb.setAdvertiserId( -1L );
                //删除操作将state状态修改为1
                delete = commonDao.updateByPrimaryKey( parkAdvertiserTb );
            }

        }
        return "{\"state\":" + delete + "}";
    }

    @Override
    public String upload(HttpServletRequest request, MultipartFile file) throws IOException {
        System.out.println( "aaaaa" );
        logger.info( "开始广告图片上传>>>>>.." );
        String description = parkingos.com.bolink.utlis.RequestUtil.processParams( request,"description" );
        String redirectURL = parkingos.com.bolink.utlis.RequestUtil.processParams( request,"redirectURL" );
        //String id = RequestUtil.processParams( request,"id" );
        Long id = parkingos.com.bolink.utlis.RequestUtil.getLong( request,"id",0L );
        //如果文件不为空，写入上传路径
        if(!file.isEmpty()) {
            //上传文件路径
            String path = request.getSession().getServletContext().getRealPath("/images/");

            //上传文件名
            String filename = file.getOriginalFilename();
            filename = UUID.randomUUID().toString().replace( "-","" )+"."+filename.split( "\\." )[1];

            //判断路径是否存在，如果不存在就创建一个
            File filepath = new File(path,id+"");
            if(!filepath.exists()){
                filepath.mkdirs();
            }

            //将上传文件保存到一个目标文件当中
            file.transferTo(new File(filepath + File.separator + filename));

            System.out.println( "图片上传成功" );

            //将广告信息保存到数据库
            AdvertisementTb advertisementTb = new AdvertisementTb();
            advertisementTb.setDescription( description );
            advertisementTb.setAdvertiserId( id );
            advertisementTb.setRedirectUrl( redirectURL );
            advertisementTb.setState( 0 );
            advertisementTb.setPath("http://ist2.sciseetech.com:8888"+request.getContextPath()+"/images/"+id+"/"+filename  );

            logger.info( "广告内容:>>>>"+advertisementTb );

            int insert = commonDao.insert( advertisementTb );
           return "{\"state\":" + insert + "}";
        } else {
            return "{\"state\":" + 0 + "}";
        }
    }

    @Override
    public JSONObject selectAdvertisementByConditions(Map<String, String> params) {
        String str = "{\"total\":0,\"page\":1,\"rows\":[]}";
        JSONObject result = JSONObject.parseObject(str);

        Long advId = Long.valueOf( params.get( "comid" ) );

        AdvertisementTb advertisementTb = new AdvertisementTb();
        advertisementTb.setAdvertiserId( advId );
        advertisementTb.setState( 0 );

        List<AdvertisementTb> list = commonDao.selectListByConditions( advertisementTb );
        if(list!=null&&list.size()!=0){

            result.put( "total",list.size() );
            result.put( "rows",list );
            if(params.get("page")!=null){
                result.put("page",Integer.parseInt(params.get("page")));
            }
        }
        return result;
    }

    @Override
    public String edit(HttpServletRequest req) {
        Long id = RequestUtil.getLong( req,"id",-1L  );
        String description = RequestUtil.processParams( req,"description" );
        String redirectUrl = RequestUtil.processParams( req,"redirectUrl" );

        if(id==-1 ){
            return "{\"state\":" + 0 + "}";
        }

        AdvertisementTb advertisementTb = new AdvertisementTb();
        advertisementTb.setId( id );
        advertisementTb.setState( 0 );

        advertisementTb= (AdvertisementTb) commonDao.selectObjectByConditions( advertisementTb );
        if(advertisementTb==null){
            return "{\"state\":" + 0 + "}";
        }

        advertisementTb.setRedirectUrl( redirectUrl );
        advertisementTb.setDescription( description );

        int count = commonDao.updateByPrimaryKey( advertisementTb );

        return "{\"state\":" + count + "}";
    }

    @Override
    public String deleteAdv(HttpServletRequest req) {

        Long id = RequestUtil.getLong( req,"id",-1L  );

        AdvertisementTb advertisementTb = new AdvertisementTb();
        advertisementTb.setId( id );
        advertisementTb.setState( 1 );

        int count = commonDao.updateByPrimaryKey( advertisementTb );
        return "{\"state\":" + count + "}";
    }
}
