<template>
    <section>
        <common-table
                :queryapi="queryapi"
                :addapi="addapi"
                :editapi="editapi"
                :delapi="delapi"
                :tableheight="tableheight"
                :fieldsstr="fieldsstr"
                :tableitems="tableitems"
                :btswidth="btswidth"
                :hide-export="hideExport"
                :hide-options="hideOptions"
                :addtitle="addtitle"
                :searchtitle="searchtitle"
                :hideTool="hideTool"
                :hideSearch="hideSearch"
                :hideAdd="hideAdd"
                :showEdit="showEdit"
                :showdelete="showdelete"
                :showAdvertiser="showAdvertiser"
                ref="bolinkuniontable"
        ></common-table>
    </section>
</template>

<script>

    import {path, checkURL, checkUpload, checkNumber} from '../../api/api';
    import util from '../../common/js/util'
    import common from '../../common/js/common'
    import CommonTable from '../../components/CommonTable'
    import {httpUrl} from '../../api/http_url'
    import axios from 'axios'
    import {AUTH_ID} from "../../common/js/const";

    export default {
        components: {       //组件加载
            CommonTable      //表格
        },
        data() {
            return {
            	showAdvertiser:true,
                loading: false,         //loading页面是否显示
                hideExport: true,       //隐藏导出
                hideSearch: true,      //隐藏查询
                 //显示日期查询
                hideAdd: true,         //隐藏添加
                tableheight: '',        //表格高度
                showdelete: true,       //显示删除
                hideOptions: false,      //隐藏多选框
                       //显示停车信息
                hideTool: false,        //隐藏工具栏
                showEdit:true,
                queryapi: '/advertiser/advertisementQuery',    //数据请求路径
                addapi: '/shop/create',
                editapi: '/advertiser/edit',
                delapi: '/advertiser/deleteAdv',
                btswidth: '100',                 //按钮宽度
                fieldsstr: 'id__name__address__phone__number__linkman',//请求数据的格式，在云平台的页面找接口和有关请求参数。
                tableitems: [                       //表格元素，表头
                    {

                        hasSubs: false,
                        subs: [{
                            label: '编号',          //页面表格显示
                            prop: 'id',             //对应表中字段
                            width: '100',           //列宽度
                            type: 'number',         //对应表中字段类型
                            editable: false,         //是否可编辑
                            searchable: true,       //是否可查询
                            addable: false,          //是否可添加
                            unsortable: true,       //是否可排序
                            align: 'center'         //页面表格内容显示位置
                        }]
                    }, {

                        hasSubs: false,
                        subs: [{
                            label: '广告描述',
                            prop: 'description',
                            width: '200',
                            type: 'str',
                            editable: true,
                            searchable: true,
                            addable: true,
                            unsortable: true,
                            align: 'center'
                        }]
                    },{

                        hasSubs: false,
                        subs: [{
                            label: '跳转URL',
                            prop: 'redirectUrl',
                            width: '200',
                            type: 'str',
                            editable: true,
                            searchable: false,
                            addable: false,
                            unsortable: true,
                            align: 'center'
                        }]
                    },{

                        hasSubs: false,
                        subs: [{
                            label: '查看广告图片',
                            prop: 'path',
                            width: '250',
                            type: 'str',
                            editable: false,
                            searchable: false,
                            addable: true,
                            unsortable: true,
                            hidden: false,
                            align: 'center'
                        }]
                    }
                ],

                addtitle: '添加广告',
                searchtitle: '搜索',
                showType:'',

            }
        },
        mounted() {
            window.onresize = () => {
                this.tableheight = common.gwh() - 143;
            };
            this.tableheight = common.gwh() - 143;
            
        },
        activated() {
            window.onresize = () => {
                this.tableheight = common.gwh() - 143;
            };
            this.tableheight = common.gwh() - 143;
            this.$refs['bolinkuniontable'].$refs['search'].resetSearch();
            this.$refs['bolinkuniontable'].getTableData({});

        },
        watch: {
            
        },
        methods: {

        },

    }
</script>

<style scoped>
    .gutter {
        display: none
    }
</style>