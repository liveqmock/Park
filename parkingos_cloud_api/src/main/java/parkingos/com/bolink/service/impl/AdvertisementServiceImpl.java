package parkingos.com.bolink.service.impl;

import com.zld.common_dao.dao.CommonDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import parkingos.com.bolink.actions.UploadadvertisementAction;
import parkingos.com.bolink.beans.AdvertisementTb;
import parkingos.com.bolink.beans.ParkAdvertiserTb;
import parkingos.com.bolink.service.AdvertisemenetService;
import parkingos.com.bolink.utlis.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AdvertisementServiceImpl implements AdvertisemenetService{


    Logger logger = Logger.getLogger(AdvertisementServiceImpl.class);
    @Autowired
    CommonDao commonDao;

    @Override
    public String upload(HttpServletRequest request, MultipartFile file) throws IOException {
        logger.info( "开始广告图片上传>>>>>.." );
        String description = RequestUtil.processParams( request,"description" );
        String redirectURL = RequestUtil.processParams( request,"redirectURL" );
        //String id = RequestUtil.processParams( request,"id" );
        Long id = RequestUtil.getLong( request,"id",0L );
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
            advertisementTb.setPath("http://ist2.sciseetech.com:8888"+request.getContextPath()+"/images/"+id+"/"+filename);

            logger.info( "广告内容:>>>>"+advertisementTb );

            commonDao.insert( advertisementTb );
            return "success";
        } else {
            return "error";
        }
    }

    @Override
    public String addParkId(HttpServletRequest request) {

        //接收参数
        Long advertiserId = RequestUtil.getLong( request,"advertiserId" ,-1L);
        Long parkId = RequestUtil.getLong( request,"parkId" ,-1L);
        if(advertiserId==-1 ||parkId==-1 ){
            return "error";
        }

        ParkAdvertiserTb parkAdvertiserTb = new ParkAdvertiserTb();
        parkAdvertiserTb.setParkId( parkId );

        int count = commonDao.selectCountByConditions( parkAdvertiserTb );
        if(count>0){
            //该停车场已经绑定广告商  只需要修改绑定广告商
            ParkAdvertiserTb bean = (ParkAdvertiserTb) commonDao.selectObjectByConditions( parkAdvertiserTb );
            bean.setAdvertiserId( advertiserId );

            commonDao.updateByPrimaryKey( bean );

            return "success";
        }

        //该停车场还没有绑定广告商  直接保存
        parkAdvertiserTb.setAdvertiserId( advertiserId );
        commonDao.insert( parkAdvertiserTb );

        return "success";
    }
}
