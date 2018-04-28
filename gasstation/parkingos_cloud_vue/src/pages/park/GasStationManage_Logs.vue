<template>
    <section>
        <common-table
                :queryapi="queryapi"
                :tableheight="tableheight"
                :fieldsstr="fieldsstr"
                :tableitems="tableitems"
                :btswidth="btswidth"
                :hide-export="hideExport"
                :hide-options="hideOptions"
                :searchtitle="searchtitle"
                :hideTool="hideTool"
                :hideSearch="hideSearch"
                :hideAdd="hideAdd"
                :showEdit="showEdit"
                :showdelete="showdelete"
                ref="bolinkuniontable"
        ></common-table>
    </section>
</template>

<script>

    import {path, openType, sendState, sdkState} from '../../api/api';
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
                loading: false,         //loading页面是否显示
                hideExport: true,       //隐藏导出
                hideSearch: false,      //隐藏查询
                 //显示日期查询
                hideAdd: true,         //隐藏添加
                tableheight: '',        //表格高度
                showdelete: true,       //显示删除
                hideOptions: true,      //隐藏多选框
                       //显示停车信息
                hideTool: false,        //隐藏工具栏
                showEdit:true,
                queryapi: '/logs/quickquery.do',    //数据请求路径
                btswidth: '100',                 //按钮宽度
                fieldsstr: 'id__parked_id__device_id__info__status__time_stamp__send_state__sdk_state',//请求数据的格式，在云平台的页面找接口和有关请求参数。
                tableitems: [                       //表格元素，表头
                    {

                        hasSubs: false,
                        subs: [{
                            label: '编号',          //页面表格显示
                            prop: 'id',             //对应表中字段
                            width: '123',           //列宽度
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
                            label: '加油站id',
                            prop: 'parked_id',
                            width: '123',
                            type: 'str',
                            editable: true,
                            searchable: true,
                            addable: true,
                            unsortable: true,
                            align: 'center'
                        }]
                    }, {

                        hasSubs: false,
                        subs: [{
                            label: '加油机id',
                            prop: 'device_id',
                            width: '200',
                            type: 'str',
                            editable: true,
                            searchable: true,
                            addable: true,
                            unsortable: true,
                            align: 'center'
                        }]
                    }, {

                        hasSubs: false,
                        subs: [{
                            label: '消息内容',
                            prop: 'info',
                            width: '300',
                            type: 'str',
                            editable: true,
                            searchable: true,
                            addable: true,
                            unsortable: true,
                            align: 'center',
                        }]
                    }, {

                        hasSubs: false,
                        subs: [{
                            label: '是否开闸',
                            prop: 'status',
                            width: '123',
                            type: 'selection',
                            selectlist: openType,
                            editable: true,
                            searchable: true,
                            addable: true,
                            unsortable: true,
                            align: 'center',
                            format:function(row){
                            	return common.nameformat(row,openType,'status');
                            }
                        }]
                    }, {

                        hasSubs: false,
                        subs: [{
                            label: '时间戳',
                            prop: 'time_stamp',
                            width: '180',
                            type: 'date',
                            editable: true,
                            searchable: true,
                            addable: true,
                            unsortable: true,
                            align: 'center',
                            format:function (row) {
                                return common.dateformat(row.time_stamp)
                            }
                        }]
                    },{

                        hasSubs: false,
                        subs: [{
                            label: '云平台tcp通知',
                            prop: 'send_state',
                            width: '180',
                            type: 'selection',
                            selectlist: sendState,
                            editable: false,
                            searchable: true,
                            addable: false,
                            unsortable: true,
                            align: 'center',
                            format:function(row){
                            	return common.nameformat(row,sendState,'send_state');
                            }
                        }]
                    },{

                        hasSubs: false,
                        subs: [{
                            label: '客户端处理结果',
                            prop: 'sdk_state',
                            width: '180',
                            type: 'selection',
                            selectlist: sdkState,
                            editable: false,
                            searchable: true,
                            addable: false,
                            unsortable: true,
                            align: 'center',
                            format:function(row){
                            	return common.nameformat(row,sdkState,'sdk_state');
                            }
                        }]
                    }
                ],

                searchtitle: '日志查询',
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