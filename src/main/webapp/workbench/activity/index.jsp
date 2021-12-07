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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){
		//bootstrap时间空间
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});
		//为创建按钮绑定事件打开添加操作的模态窗口
		$("#addBtn").click(function () {



			$.ajax({
				url:"workbench/activity/getUserList.do",
				type:"get",
				dataType:"json",
				success:function (res) {
					var html = "";
					console.log(res);
					$.each(res,function (i,n) {
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})
					$("#create-owner").html(html);
				}
			})
			var id = "${user.id}"
			//在js中使用el表达式，一定要在字符串中
			$("#create-owner").val(id)
			//找到需要操作的模态窗口的jquert对象，调用modal方法,show/hide
			$("#createActivityModal").modal("show");
		})

		//为修改按钮绑定事件打开修改操作的模态窗口
		$("#editBtn").click(function () {
			var $xz = $("input[name=xz]:checked")
			if($xz.length==0){
				alert("请选择需要修改的记录")
			}else if($xz.length>1){
				alert("只能选择一条记录进行修改")
			}else {
				var id = $xz.val();

				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{"id":id},
					dataType:"json",
					type:"get",
					success:function (res) {
						//res:{"userList":[{},{},{}],"activity":{市场活动}}
						//处理所有者下拉框
						var html = "<option></option>";
						$.each(res.userList,function (i,n) {
							html+="<option value='"+n.id+"'>"+n.name+"</option>"
						})
						$("#edit-owner").html(html)
						//单条activity
						var $options = $("#edit-owner>option")
						$.each($options,function (i,n) {
							if (res.activity.owner == n.value){
								$(this).attr("selected","selected")
							}
						})
						$("#edit-name").val(res.activity.name)
						$("#edit-id").val(res.activity.id)
						$("#edit-startDate").val(res.activity.startDate)
						$("#edit-endDate").val(res.activity.endDate)
						$("#edit-cost").val(res.activity.cost)
						$("#edit-description").val(res.activity.description)
						$("#editActivityModal").modal("show");
					}
				})

			}

		})

		//保存市场活动
		$("#saveBtn").click(function () {
			$.ajax({
				url:"workbench/activity/saveActivity.do",
				data:{
					"owner":$.trim($("#create-owner").val()),
					"name":$.trim($("#create-name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val()),
				},
				type:"post",
				dataType: "json",
				success:function (res) {
					if(res.success){
						alert("修改市场活动成功")
						//添加成功后，刷新市场活动信息列表
						//第一个参，操作后停留在当前页；第二个参数操作后停留在当前页
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//刷新表单
						//jquery没有提供reset，但是原生js提供了
						$("#addActivity")[0].reset();

						//关闭模态窗口
						$("#createActivityModal").modal("hide");
					}else{
						alert("添加市场活动失败")
					}
				}
			})
		})

		//更新市场活动
		/*
			实际开发中先做添加再做修改的操作，为了节省时间一般都是copy添加
		*/
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
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭模态窗口
						$("#editActivityModal").modal("hide");
					}else{
						alert("修改市场活动失败")
					}
				}
			})
		})

		// 页面加载完毕后触发一个方法
		pageList(1,5);

		//为查询按钮绑定事件触发pageList方法
		$("#searchBtn").click(function () {

			//点击查询按钮的时候，我们应该将搜索框中的信息保存起来，保存到隐藏域中
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

		})

		//为全选的复选框绑定事件，触发全选操作
		$("#checkAll").click(function () {
			$("input[name=xz]").prop("checked",this.checked)
		})
		//动态生成的元素不能以普通绑定的事件
		/*
			动态生成的元素，我们要以on方法的形式来触发事件
			语法：
				$(绑定元素).on(绑定事件，绑定元素的jquery对象，回调函数)
		 */
		$("#activityBody").on("click",$("input[name=xz]"),function () {
			$("#checkAll").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
		})

		//为删除按钮绑定事件，执行市场活动删除操作
		$("#deleteBtn").click(function () {
			var $xz = $("input[name=xz]:checked")
			if($xz.length==0){
				alert("请选择需要删除的记录")
			}else{
				if (confirm("确定删除所选择的记录吗？此操作也将删除跟此活动关联的线索")){
					//拼接参数
					var param = "";
					//将$xz中每一个dom对象都取出来，取其value值
					for(var i=0;i<$xz.length;i++){
						param += "ids="+$($xz[i]).val();
						//如果不是最后一个元素，追加&
						if(i<$xz.length-1){
							param += "&";
						}
					}
					$.ajax({
						url:"workbench/activity/deleteActivity.do",
						data:param,
						dataType:"json",
						type:"post",
						success:function (res) {
							if (res.success){
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
								alert("删除成功")
							}else {
								alert("删除市场活动失败")
							}
						}
					})
				}

			}
		})

	});

	/*
		对于所有的关系型数据库，做前端的分页相关基础组件
		就是pageNo和pageSize
		pageNo：页码
		pageSize：每页展现的数据库
		pageList方法： 发出ajax请求到后台，从后台取得最新的市场活动信息列表数据，局部刷新市场活动信息列表

		都在哪些情况下需要刷新列表
		1、点击左侧菜单的市场活动
		2、创建、修改、删除后
		3、点击查询按钮的时候
		4、点击分页组件的时候
	 */
	function pageList(pageNo,pageSize) {
		//将全选取消
		$("#checkAll").prop("checked",false);

		//查询前将隐藏域中保存的信息取出来重新赋值
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"endDate":$.trim($("#search-endDate").val())
			},
			type:"get",
			dataType:"json",
			success:function (res) {
				/*
				res:
				1、[{活动1},{活动2},{活动3}]
				2、分页插件需要的，查询出来的总记录数
				{"total":100,"datalist":[{活动1},{活动2},{活动3}]}
				 */
				var html = ""
				$.each(res.dataList,function (i,n) {
					html += '<tr class="active">'
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>'
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>'
					html += '<td>'+n.owner+'</td>'
					html += '<td>'+n.startDate+'</td>'
					html += '<td>'+n.endDate+'</td>'
					html += '</tr>'
				})
				$("#activityBody").html(html);
				//计算总页数
				var totalPages = res.total%pageSize==0?res.total/pageSize:parseInt(res.total/pageSize)+1;
				//数据处理完毕后结合分页插件，对数据进行分页
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: res.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,
					//回调函数，再点击分页组件时触发
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})
	}
	
</script>
</head>
<body>
	<input type="hidden" id="hidden-name">
	<input type="hidden" id="hidden-owner">
	<input type="hidden" id="hidden-startDate">
	<input type="hidden" id="hidden-endDate">
	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" id="addActivity" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
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
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
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
<%--								关于文本域textarea：
									（1）一定要以标签对的形式来呈现，正常情况下标签对要紧挨着
									（2）textarea虽然是以标签对的形式来呈现的，但是他也是属于表单元素范畴
									（3）我们所有对textarea的取值和赋值操作，应该统一使用val()方法,而不是html()
									--%>
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
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control time" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control time" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
<%--
					点击创建按钮，观察两个属性和属性值
					data-toggle="modal":表示触发该按钮，将要打开一个模态窗口
					data-target="#editActivityModal：表示要打开哪个模态窗口
--%>
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="checkAll"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

				<div id="activityPage">

				</div>

			</div>
			
		</div>
		
	</div>
</body>
</html>