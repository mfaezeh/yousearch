<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="it">
<head>
<title>YouSearch - Clustered search 4 YouTube</title>
<script type="text/javascript" src="/YouSearch/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="/YouSearch/js/jquery.progressbar.min.js"></script>
<style>
body{
	font-family:Arial,Helvetica,sans-serif;
}
.progressBar{
	display : none;
}

.video_title{
	color: red;
	font-size:18px;
	font-weight: bolder; 
	font-variant: small-caps; 
	text-transform: lowercase;
	position:relative;
	top: -40px;
	left: 50px;
}
.video_inside{
	position:relative;
	top:-150px;
	left: 450px;
}
.video_tags{
	position:relative;
	top:-150px;
	left: 450px;
	width:200px;
}
.video_tag{
	color:#0033CC;
	font-size:12px;
	font-size-adjust:none;
	font-style:normal;
	font-variant:normal;
	font-weight:bold;
	line-height:normal;
}
.video_thumbs{
	position:relative;
	top:-40px;
	left: 50px;
}
.video_thumb{
	width: 120px;
	height: 90px;
}

.video_copy_box {
  position: fixed;
  width: 100%;
  height: 30px;
  top: auto;
  right: 0;
  bottom: 0;
  left: 0;
	background-color:#e20000;
	border-style:solid;
	border-width:2px;
	border-color:white;
}
.video_copy_text {
	color:white;
	font-weight:bold;
}
.video_break{
	background-image: url() repeat-x;
}
.result_box{
  width: 100%;
  top: auto;
  right: 0;
  bottom: 0;
  left: 0;
}
.search_box_top {
	z-index:9999;
	color:black;
	font-weight:bold;
  position: fixed;
  width: 100%;
  height: 30px;
  top: 0;
  right: 0;
  left: 0;
	background-color:#e20000;
	border-style:solid;
	border-width:2px;
	border-color:white;
}
.search_box_path{
	position:fixed;
	left: 10px;
	font-size:24px;
}
.search_box_form{
	position:fixed;
	top:4px;
	right: 10px;
}
.search_box_path a {
	color:white;
}
a {
	text-decoration:none;
}
a:hover {
	text-decoration:underline;
}
</style>
<script type="text/javascript">
	function resolveQuery(){
		var keyw = document.getElementById("input_search").value;
	
		if(keyw != ""){
			window.location = "/YouSearch/cluster/"+keyw;
		}
	}
</script>
</head>
<body>

<div class="search_box_top">
	<div class="search_box_path"><a href="http://www.youtube.com">YouTube</a> / <a href="http://SERVER/YouSearch/">YouSearch</a> / <a href="http://SERVER/YouSearch/cluster/KEYWORD">KEYWORD</a> / <span style="color:yellow">clusters</span></div>
	<span class="search_box_form"><form action="http://SERVER/YouSearch/" onsubmit="resolveQuery(); return false;"><input id="input_search" type="text" size="50"><input  type="button" value="Submit" onclick="resolveQuery()"></form></span>
</div>

<div id="search_box">
	<script type="text/javascript">
		$(document).ready(function() {
		var timerId;
		var progressBarValue = 0;
		var counts = 0;
		var loading = ["Clustering ","Clustering .","Clustering ..","Clustering ..."];
		$("#searchbar").progressBar(0,{ showText:false, barImage: '/YouSearch/images/progressbg_green.gif'} );
		$.get("http://SERVER/YouSearch/search/KEYWORD",{},
						function(data){
								clearInterval(timerId);
								$('#result_box').html(data).hide();
								$("#searchbar").progressBar(100);
								$("#searchbar").hide();
								$("#result_box").attr( "align", "left" );
								$('#result_box').show();
					});
				
				timerId = setInterval(
						function(){
							$("#searchbar").progressBar((progressBarValue++)%100,{showText: false})
							$("#searchtext").html(loading[counts++%4]);
						},500);
				
		});							
	</script>
</div>

<div id="result_box" class="result_box" align="center">
Searching...
<div align="left" class="progressbar" id="searchbar" style="width:100px;position:relative; top:200px;"></div><br/>
<div align="left" id="searchtext" style="width:100px;font-weight:bold; position:relative; top:200px;"></div>

</div>

<div class="video_copy_box" align="center">
<span class="video_copy_text">A.Scipioni - N.Dorigatti &copy; Data Mining Course - Spring 2009</span>
</div>

</body>
</html>

