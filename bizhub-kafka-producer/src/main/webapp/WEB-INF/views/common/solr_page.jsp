<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="pages border-top">
	<div class="row">
		<div class="col-md-4">
			<div class="m-t-md">
				<c:choose>
					<c:when test="${(page.totalPages-1) eq page.number }">
						当前显示 ${(page.number)*page.size+1 } 到 ${page.totalElements } 条，共 ${page.totalPages } 页 ${page.totalElements } 条
					</c:when>
					<c:otherwise>
						当前显示 ${(page.number)*page.size+1 } 到 ${(page.number+1)*(page.size) } 条，共 ${page.totalPages } 页 ${page.totalElements } 条
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="col-md-8 footable-visible">
			<ul class="pagination pull-right">
				<li class="footable-page-arrow disabled"><a data-page="0" href="javascript:void(0);" onclick="goPage(this,'${param.formId }','${param.showPageId }');">«</a></li>
				<li class="footable-page-arrow disabled"><a data-page="${(page.number-2)<0?1:(page.number-2) }" href="javascript:void(0);" onclick="goPage(this,'${param.formId }','${param.showPageId }');">‹</a></li>
				<c:forEach var="pgnum" items="${navigatepageNums }">
					<c:choose>
						<c:when test="${pgnum eq page.number }">
							<li class="footable-page active"><a data-page="${pgnum }" href="javascript:void(0);" onclick="goPage(this,'${param.formId }','${param.showPageId }');">${pgnum }</a></li>
						</c:when>
						<c:otherwise>
							<li class="footable-page"><a data-page="${pgnum }" href="javascript:void(0);" onclick="goPage(this,'${param.formId }','${param.showPageId }');">${pgnum }</a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<li class="footable-page-arrow"><a data-page="${(page.number)>page.totalPages?page.totalPages:(page.number) }" href="javascript:void(0);" onclick="goPage(this,'${param.formId }','${param.showPageId }');">›</a></li>
				<li class="footable-page-arrow"><a data-page="${page.totalPages-1 }" href="javascript:void(0);" onclick="goPage(this,'${param.formId }','${param.showPageId }');">»</a></li>
			</ul>
		</div>
		<input type="hidden" name="pageNum" />
	</div>
</div>
<script type="text/javascript">
  function goPage(objA, formId, showPageId) {
    $('#' + formId + " input[name='pageNum']").val($(objA).attr("data-page"));
    $.ajax({
      cache: true,
      type: "POST",
      url: $("#" + formId).attr("action"),
      data: $('#' + formId).serialize(),// 序列化的form
      async: false,
      error: function(data) {
        toastr.error('', '分页查询失败');
      },
      success: function(data) {
        $("#" + showPageId).html(data);
      }
    });
  }
</script>
