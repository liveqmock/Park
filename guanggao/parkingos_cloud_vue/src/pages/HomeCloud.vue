<template>
    <el-row class="container">
        <el-col :span="24" class="header not-print">
            <!--
            <el-col :span="10" class="logo" :class="collapsed?'logo-collapse-width':'logo-width'" style="text-align: center">
                {{collapsed?'':sysName}}
            </el-col>
            -->
            <el-col :span="17">

                <div style="margin-left:5px;font-size:30px;postition:relative;line-height:50px;vertical-align:middle;float:left;font-family:STXinwei">
                    &nbsp;&nbsp;&nbsp;&nbsp;广告模块后台管理
                </div>
            </el-col>

            <!--
            <el-col :span="10" class="tools">

                <div class="tools" @click.prevent="collapse">
                    <i class="el-icon-d-arrow-left" v-show="left"></i>
                    <i class="el-icon-d-arrow-right" v-show="right"></i>
                </div>

            </el-col>-->

            <el-col :span="7" style="padding-right:10px">
                <!--<div style="color:#fff;font-size:15px;display:inline;right:235px;position:absolute">{{nickname}}:-->
                <!--{{sysUserName}}-->
                <!--</div>-->
                <!--<el-menu style="height:50px" :default-active="active" theme="dark"-->
                <!--mode="horizontal" @select="selectTop">-->

                <!--<el-menu-item index="/loginCloud"><span style="color:#fff;font-size:13px">退出登录</span></el-menu-item>-->

                <!--</el-menu>-->
                <div style="color:#fff;font-size:15px;display:inline;right:235px;position:absolute">{{nickname}}:
                    {{sysUserName}}
                </div>
                <el-menu
                        style="height:50px;width: 100px;float: right;background-color:#2d3a4b;border-right: solid 0px #e6e6e6;"
                        @select="selectTop">

                    <el-menu-item index="/loginCloud" style="height:50px;"><span
                            style="color:#fff;font-size:13px;float: right;height:50px;color: #109EFF">退出登录</span>
                        <!--<el-menu-item @click="logout"><span style="color:#fff;font-size:13px;float: right;">退出登录</span>-->
                    </el-menu-item>

                </el-menu>


            </el-col>

        </el-col>
        <el-col :span="24" class="main">
            <aside :class="collapsed?'menu-collapsed':'menu-expanded'" style="overflow-y: auto;margin-left: 12px">

                <el-menu class="el-menu-vertical-demo not-print" @open="handleopen"
                         @close="handleclose"
                         @select="handleselect"
                         unique-opened v-show="!collapsed"

                         :default-active="highlightindex">

                    <el-row v-show="park">
                        <el-submenu v-if="Shop_Manage" index="/orderManage">
                            <template slot="title"><span class="menuitem">广告商管理</span></template>
                            
                            <el-menu-item index="/Shop_Message" v-if="shopManage">广告商信息
                            </el-menu-item>
                           
                        </el-submenu>
                        
                        <el-submenu v-if="Advertiser_Manage" index="/orderManage">
                            <template slot="title"><span class="menuitem">广告管理</span></template>                         
                            <el-menu-item index="/Advertiser_Message" v-if="advertiserMessage">广告信息管理
                            </el-menu-item>
                            
                            <el-menu-item index="/Park_Message" v-if="advertiserMessage">停车场管理
                            </el-menu-item>
                           
                        </el-submenu>
                
                    </el-row>
                </el-menu>
            </aside>
            <section class="content-container">
                <div class="grid-content bg-purple-light">
                    <el-col :span="24" class="content-wrapper">
                        <keep-alive>
                            <router-view></router-view>

                        </keep-alive>
                    </el-col>
                </div>
            </section>
        </el-col>
    </el-row>
</template>

<script>
    import common from '../common/js/common'
    import {AUTH_ID, showParkItem_const, AUTH_ID_UNION, showUnionItem_const, ROLE_ID} from '../common/js/const'

    export default {
        data() {
            return {
            	advertiserMessage:false,
            	Advertiser_Manage:false,
            	Shop_Manage:false,
            	shopManage:false,
            	GasStationManage:true,
            	ConnectionsManage:false,
            	LogsManage:true,
            	MessageManage:true,
                activeIndex: '/loginCloud',
                active: '',
                bolink: false,
                park: true,
                union: false,
                platform: false,
                left: true,
                right: false,
                sysName: '联盟管理后台',
                server: '',
                collapsed: false,
                sysUserName: '',
                nickname: '欢迎登陆',
                user: '',
                form: {
                    name: '',
                    region: '',
                    date1: '',
                    date2: '',
                    delivery: false,
                    type: [],
                    resource: '',
                    desc: ''
                },
                secureVisible: false,
                //根据权限控制页面是否显示
                showParkItem: sessionStorage.getItem('showParkItem') == null ? showParkItem_const : JSON.parse(sessionStorage.getItem('showParkItem')),
                showUnionItem: sessionStorage.getItem('showUnionItem') == null ? showUnionItem_const : JSON.parse(sessionStorage.getItem('showUnionItem')),
                expandindex: '',   //'/order',//展开的sub_menu
                highlightindex: sessionStorage.getItem('highlightindex'),//'/orderManage_Poles',//高亮的item
            }
        },
        methods: {
            openSecurity() {
                this.active = '/securitycenter'
                console.log(this.active)
                this.$router.push('/securitycenter')
            },
            handleopen() {
                //console.log('handleopen');
            },
            handleclose() {
                //console.log('handleclose');
            },
            selectTop(a, b) {
                //console.log(a)
                //console.log(b)
                console.log(this.active)
                this.active = a
                this.$router.push(a);
                console.log(this.active)
            },
            handleselect: function (a, b) {
                // console.log(this.active)
                // console.log(a)
                // console.log(b)
                // console.log(this.$router)
                if (a == 'centerMonitor') {
                    let routetocm = 'http://test.bolink.club/tcbcloud/monitor.do?loginuin=' + sessionStorage.getItem('loginuin');
                    let comid = sessionStorage.getItem('comid');
                    let groupid = sessionStorage.getItem('groupid');
                    if (comid != '' && comid != 'undefined') {
                        console.log(comid)
                        routetocm = routetocm + '&comid=' + comid;
                    }
                    if (groupid != '' && groupid != 'undefined') {
                        console.log(groupid)
                        routetocm = routetocm + '&groupid=' + groupid;
                    }
                    console.log(routetocm)
                    window.open(routetocm);
                    return;
                }
                var cpath = this.$router.currentRoute.fullPath

                //console.log(cpath)
                var options = this.$router.options.routes
                // this.highlightindex = a;
                this.expandindex = a.split('_')[0];
                // console.log('>>>' + a)
                // console.log('>>>' + a.split('_')[0])
                this.$router.push(a);
            },
            //退出登录
            logout: function () {
                var _this = this;
                let user = sessionStorage.getItem('user');
                let u = JSON.parse(user);
                let logoutParams = {userid: u.userid, token: sessionStorage.getItem("token")}
                this.$confirm('确认退出吗?', '提示', {
                    //type: 'warning'
                }).then(() => {
                    //this.$post(path+"/user/dologout",logoutParams)
                    sessionStorage.removeItem('user');
                    sessionStorage.removeItem('token');
                    _this.$router.push('/login');
                }).catch(() => {

                });
            },
            //折叠导航栏
            collapse: function () {
                this.collapsed = !this.collapsed;
                if (this.left == false) {
                    this.left = true;
                    this.right = false;
                } else {
                    this.left = false;
                    this.right = true;
                }

            },
            showMenu(i, status) {
                this.$refs.menuCollapsed.getElementsByClassName('submenu-hook-' + i)[0].style.display = status ? 'block' : 'none';
            },
        },
        mounted() {
            console.log('home  mounted')
            let vm = this;
            let user = sessionStorage.getItem('user');
			
            if (user) {
                user = JSON.parse(user);
                if(user.strid=="admin"){
                	this.advertiserMessage=true;
            		this.Advertiser_Manage=true;
            		this.Shop_Manage=true;
            		this.shopManage=true;
                }else{
                	this.advertiserMessage=true;
            		this.Advertiser_Manage=true;
                }
                this.sysUserName = user.nickname || '';
				
                var cpath = this.$router.currentRoute.fullPath;
                console.log(cpath)
                this.highlightindex = cpath;
                if (cpath == '/query/queryout') {
                    this.active = '/query/queryin'
                } else if (cpath == '/order/orderout') {
                    this.active = '/order/orderin'
                } else {
                    this.active = cpath;
                }
                if (user.oid == 0 || user.oid == ROLE_ID.PARK) {
                    this.nickname = "车场";
                    this.park = true;
                }
                if (user.oid == ROLE_ID.UNION) {
                    this.nickname = "集团";
                    this.union = true;
                }

            }
        },
        activated() {
            console.log('home active')
        },
        watch: {
            ulist: function (val) {
                this.sysUserName = val.nickname
            }
        },

    }

</script>
<style lang="scss" scoped>

    $bg: #2d3a4b; //#008F4C; //#324157;//#0080dd;//#35495E;//#1F2D3D
    .container {
        position: absolute;
        top: 0px;
        bottom: 0px;

        width: 100%;
        .header {
            height: 50px;
            line-height: 50px;

            background: $bg;
            color: #fff;
            .userinfo {
                text-align: right;
                padding-right: 20px;
                float: right;
                .userinfo-inner {
                    cursor: pointer;
                    color: #fff;
                    img {
                        width: 40px;
                        height: 40px;
                        border-radius: 20px;
                        margin: 10px 0px 10px 10px;
                        float: right;
                    }
                }
            }
            .logo {
                //width:180px;
                height: 50px;
                font-size: 22px;
                padding-left: 20px;
                padding-right: 20px;
                border-color: rgba(238, 241, 146, 0.3);
                border-right-width: 1px;
                border-right-style: solid;
                img {
                    width: 40px;
                    float: left;
                    margin: 10px 10px 10px 18px;
                }
                .txt {
                    color: #fff;
                }
            }
            .logo-width {
                width: 180px;
            }
            .logo-collapse-width {
                width: 60px
            }
            .tools {
                padding: 0px 23px;
                width: 14px;
                height: 50px;
                line-height: 50px;
                cursor: pointer;
            }
        }
        .main {
            display: flex;
            // background: #324057;
            position: absolute;
            top: 50px;
            bottom: 0px;
            overflow: hidden;
            aside {
                background: #EEF1F6;
                flex: 0 0 180px;
                width: 180px;
                // position: absolute;
                // top: 0px;
                // bottom: 0px;
                .el-menu {
                    height: 100%;

                }

                .collapsed {
                    width: 60px;
                    .item {
                        position: relative;
                    }
                    .submenu {
                        position: absolute;
                        top: 0px;
                        left: 60px;
                        z-index: 99999;
                        height: auto;
                        display: none;
                    }

                }
            }
            .menuitem {
                font-size: 16px;
                /*margin-left: 12px;*/
                //color:black
            }
            .menu-collapsed {
                flex: 0 0 60px;
                width: 60px;
            }
            .menu-expanded {
                flex: 0 0 180px;
                width: 80px;
            }
            .content-container {
                // background: #f1f2f7;
                flex: 1;
                // position: absolute;
                // right: 0px;
                // top: 0px;
                // bottom: 0px;
                // left: 180px;
                overflow-y: hidden;
                padding: 10px;
                padding-top: 8px;
                .breadcrumb-container {
                    //margin-bottom: 15px;
                    .title {
                        width: 180px;
                        float: left;
                        color: #475669;
                    }
                    .title2 {
                        width: 160px;
                        float: left;
                        color: #475669;
                    }
                    .breadcrumb-inner {
                        float: right;
                    }
                }
                .content-wrapper {
                    background-color: #fff;
                    box-sizing: border-box;
                }
            }
        }
    }

    /*el-menu-item选中加粗 左侧item*/
    .el-menu--horizontal.el-menu--dark .el-submenu .el-menu-item.is-active, .el-menu-item.is-active {
        font-weight: bold;
    }

    @media print {
        .not-print {
            /*opacity: 0*/
        }
    }
</style>
