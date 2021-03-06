<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
	<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){

		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});

		//为修改按钮绑定
		$("#editBtn").click(function () {
			$.ajax({
				url:"workbench/activity/getUserList.do",
				date:{"id":'${activity.id}'},
				type:"get",
				dataType:"json",
				success:function (res) {
					var html = "";
					console.log(res);
					$.each(res,function (i,n) {
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})
					$("#edit-owner").html(html);
                    var $options = $("#edit-owner>option")
                    $.each($options,function (i,n) {
                        if (res.clue.owner == n.value){
                            $(this).attr("selected","selected")
                        }
                    })
					//单条activity
					$("#edit-id").val("${activity.id}")
					$("#edit-name").val("${activity.name}")
					$("#edit-owner").val("${activity.owner}")
					$("#edit-startDate").val(${activity.startDate})
					$("#edit-endDate").val(${activity.endDate})
					$("#edit-cost").val(${activity.cost})
					$("#edit-description").val("${activity.description}")
					$("")
					$("#editActivityModal").modal("show");
				}
			})

		})

		//修改事件
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/activity/updateActivity.do",
				data:{
					"id":$.trim($("#edit-id").val()),
					"owner":$.trim($("#edit-owner").val()),
					"name":$.trim($("#edit-name").val()),
					"startDate":$.trim($("#edit-startDate").val()),
					"endDate":$.trim($("#edit-endDate").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-description").val()),
				},
				type:"post",
				dataType: "json",
				success:function (res) {
					if(res.success){
						//修改成功后，刷新市场活动信息列表
						alert("修改市场活动成功")
						//关闭模态窗口
						$("#editActivityModal").modal("hide");
						window.location.href = "workbench/activity/detail.do?id="+$("#edit-id").val()

					}else{
						alert("修改市场活动失败")
					}
				}
			})
		})

		//删除事件
		$("#deleteBtn").click(function () {
				if (confirm("确定删除这条记录吗？")) {
					var param = "ids=" + "${activity.id}"
					$.ajax({
						url: "workbench/activity/deleteActivity.do",
						data: param,
						dataType: "json",
						type: "post",
						success: function (res) {
							if (res.success) {
								alert("删除成功")
								window.location.href ="/crm/workbench/activity/index.jsp"
							} else {
								alert("删除市场活动失败")
							}
						}
					})

				}

		})

		//页面加载完毕后，展现该市场活动关联的备注信息列表
		showRemarkList();

        $("#remarkBody").on("mouseover",".remarkDiv",function(){
            $(this).children("div").children("div").show();
        })
        $("#remarkBody").on("mouseout",".remarkDiv",function(){
            $(this).children("div").children("div").hide();
        })

        //保存按钮绑定事件，执行备注的添加
        $("#saveRemarkBtn").click(function () {
            $.ajax({
                url:"workbench/activity/saveRemark.do",
                data:{
                    "noteContent":$.trim($("#remark").val()),
                    "activityId":"${activity.id}"
                },
                dataType:"json",
                type:"post",
                success:function (res) {
                    if (res.success){
                        $("#remark").val("")
                        alert("添加备注成功")
                        var html=""
                        html += '<div id="'+ res.remarkInfo.id +'" class="remarkDiv" style="height: 60px;">'
                        html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">'
                        html += '<div style="position: relative; top: -40px; left: 40px;" >'
                        html += '<h5  id="h5'+ res.remarkInfo.id +'">'+res.remarkInfo.noteContent+'</h5>'
                        html += '<font color="gray">市场活动</font> '
                        html +=	' <font color="gray">-</font> '
                        html += '${activity.name}'
                        html += '<small style="color: gray;">'+ res.remarkInfo.createTime +'&nbsp;由&nbsp;'
                        html +=  res.remarkInfo.createBy+"</small>"
                        html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">'
                        html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\''+res.remarkInfo.id+'\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color:orangered;"></span></a>'
                        html += '&nbsp;&nbsp;&nbsp;&nbsp;'
                        html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\''+res.remarkInfo.id+'\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color:orangered;"></span></a>'
                        html += '</div>'
                        html += '</div>'
                        html += '</div>'

                        $("#bz").append(html)
                    }else {
                        alert("添加备注失败")
                    }
                }
            })
        })

        //为更新按钮添加操作
        $("#updateRemarkBtn").click(function () {
            var id = $("#remarkId").val()
            $.ajax({
                url:"workbench/activity/updateRemark.do",
                data:{
                    "noteContent":$.trim($("#noteContent").val()),
                    "id":id
                },
                dataType:"json",
                type:"post",
                success:function (res) {
                    if (res.success){
                        alert("修改备注成功")
                        showRemarkList()
                        $("#editRemarkModal").modal("hide");
                    }else {
                        alert("修改备注失败")
                    }
                }
            })
        })

    });

	function showRemarkList() {

		$.ajax({
			url:"workbench/activity/getRemarkListByAId.do",
			data:{
				"activityId":"${activity.id}",
			},
			dataType:"json",
			type:"get",
			success:function (res) {
				var html = ""
				$.each(res,function (i,n) {

				html += '<div id="'+n.id+'" class="remarkDiv" style="height: 60px;">'
				html += '<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">'
				html += '<div style="position: relative; top: -40px; left: 40px;" >'
				html += '<h5 id="h5'+ n.id +'">'+n.noteContent+'</h5>'
				html += '<font color="gray">市场活动</font> '
				html +=	' <font color="gray">-</font> '
				html += '${activity.name}'
				html += '<small style="color: gray;">'+ (n.editFlag==0?n.createTime:n.editTime) +'&nbsp;由&nbsp;'
				html +=  (n.editFlag==0?n.createBy:n.editBy) +"</small>"
				html += '<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">'
				html += '<a class="myHref" href="javascript:void(0);" onclick="editRemark(\''+n.id+'\')"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color:orangered;"></span></a>'
				html += '&nbsp;&nbsp;&nbsp;&nbsp;'
				html += '<a class="myHref" href="javascript:void(0);" onclick="deleteRemark(\''+n.id+'\')"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color:orangered;"></span></a>'
				html += '</div>'
				html += '</div>'
				html += '</div>'

				})
				$("#bz").html(html)
			}
		})

	}

    function deleteRemark(id) {
        $.ajax({
            url:"workbench/activity/deleteRemark.do",
            data:{
                "id":id
            },
            dataType:"json",
            type:"get",
            success:function (res) {
                if (res.success){
                    alert("删除信息成功")
                    showRemarkList();
                }else {
                    alert("删除备注信息失败")
                }
            }
        })
    }

    function editRemark(id) {
	    $("#remarkId").val(id)
	    var noteContent = $("#h5"+id).html();
	    $("#noteContent").val(noteContent)
        $("#editRemarkModal").modal("show");
	    showRemarkList()
    }
</script>

</head>
<body>

	<!-- 修改市场活动备注的模态窗口 -->
	<div class="modal fade" id="editRemarkModal" role="dialog">
		<%-- 备注的id --%>
		<input type="hidden" id="remarkId">
        <div class="modal-dialog" role="document" style="width: 40%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel1">修改备注</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">内容</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="noteContent"></textarea>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateRemarkBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 修改市场活动的模态窗口 -->
    <div class="modal fade" id="editActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">修改市场活动</h4>
                </div>
                <div class="modal-body">
					<input type="hidden" id="edit-id">
                    <form class="form-horizontal" role="form">

                        <div class="form-group">
                            <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-owner">

                                </select>
                            </div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-startDate">
                            </div>
                            <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-endDate">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-cost">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-description"></textarea>
                            </div>
                        </div>

                    </form>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>市场活动-${activity.name} <small>${activity.startDate} ~ ${activity.endDate}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: -70px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${activity.name}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>

		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">开始日期</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.startDate}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">结束日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${activity.endDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">成本</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${activity.cost}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${activity.createBy}</b><small style="font-size: 10px; color: gray;">${activity.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${activity.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${activity.editTime}&nbsp;&nbsp;</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${activity.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div id="remarkBody" style="position: relative; top: 30px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
        <div id="bz">

        </div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary" id="saveRemarkBtn">保存</button>
				</p>
			</form>
		</div>
	</div>
	<div style="height: 200px;"></div>
</body>
</html>