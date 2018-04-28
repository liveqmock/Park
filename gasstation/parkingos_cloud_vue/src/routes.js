// import Login from './pages/Login.vue'
import LoginCloud from './pages/LoginCloud.vue';
import NotFound from './pages/404.vue';
// import Home from './pages/Home.vue'
import HomeCloud from './pages/HomeCloud.vue';

//加油站
import index from './pages/park/index.vue';
import GasStationManage_Message from './pages/park/GasStationManage_Message.vue';
import GasStationManage_Logs from './pages/park/GasStationManage_Logs.vue';
import GasStationManage_Connection from './pages/park/GasStationManage_Connection.vue';

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
            {path: '/GasStationManage_Message', component: GasStationManage_Message, name: '加油站信息'},
            {path: '/GasStationManage_Logs', component: GasStationManage_Logs, name: '加油站日志'},
            {path: '/GasStationManage_Connection', component: GasStationManage_Connection, name: '加油站连接'},
        ]
    }
];

export default routes;
