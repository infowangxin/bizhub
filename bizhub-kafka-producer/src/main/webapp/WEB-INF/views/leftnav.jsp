<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="sidebar-scroll">
	<div class="logo">
		<a href="index.html"><img src="${ctx }/static/images/logo_icon.png" alt="">examples</a>
	</div>
	<div class="sidebar-collapse">
		<div class="nav-header" id="side-head">
			<div class="dropdown profile-element text-center">
				<span><img alt="image" class="img-circle " src="${ctx }/static/img/profile_small.jpg" /></span> <a data-toggle="dropdown" class="dropdown-toggle" href="#"><span class="clear"> <span class="block m-t-xs"> <strong class="font-bold">Jack</strong></span>
						<span class="text-muted text-xs block">用户设置<b class="caret"></b></span>
				</span> </a>
				<ul class="dropdown-menu animated fadeInRight m-t-xs">
					<li><a href="javascript:void(0)">Profile</a></li>
					<li><a href="javascript:void(0)">Contacts</a></li>
					<li><a href="javascript:void(0)">Mailbox</a></li>
					<li class="divider"></li>
					<li><a href="javascript:void(0)">Logout</a></li>
				</ul>
			</div>
		</div>
		<ul class="nav metismenu" id="side-menu">
			<li><a href="#"><i class="fa fa-sitemap"></i> <span class="nav-label">操作solrcloud6.5 </span><span class="fa arrow"></a>
				<ul class="nav nav-second-level collapse">
					<li><a href="${ctx }/item/add"><span class="nav-label">新增</span></a></li>
					<li><a href="${ctx }/item/list"><span class="nav-label">查询</span></a></li>
				</ul></li>
			<li><a href="#"><i class="fa fa-sitemap"></i> <span class="nav-label">测试 </span><span class="fa arrow"></span></a>
				<ul class="nav nav-second-level collapse">
					<li><a href="${ctx }/news/add"><span class="nav-label">新增</span></a></li>
					<li><a href="${ctx }/news/list"><span class="nav-label">查询</span></a></li>
				</ul></li>
		</ul>
	</div>
</div>