// import Login from './pages/Login.vue'
import LoginCloud from './pages/LoginCloud.vue';
import NotFound from './pages/404.vue';
// import Home from './pages/Home.vue'
import HomeCloud from './pages/HomeCloud.vue';

//加油站
import index from './pages/park/index.vue';
import Shop_Message from './pages/park/Shop_Message.vue';
import Advertiser_Message from './pages/park/Advertiser_Message.vue';
import Park_Message from './pages/park/Park_Message.vue';

let routes = [

    {
        path: '/loginCloud',
        component: LoginCloud,
        name: '',
        hidden: true
    },
    /*
    * 以下是云平台页面
    * 加油站
    *
    * */

    {
        path: '/',
        component: HomeCloud,
        // name: '加油站管理',
        iconCls: 'el-icon-document',
        children: [
            {path: '/Shop_Message', component: Shop_Message, name: '加油站信息'},
            {path: '/Advertiser_Message', component: Advertiser_Message, name: '加油站日志'},
            {path: '/Park_Message', component: Park_Message, name: '加油站连接'},
        ]
    }
];

export default routes;
