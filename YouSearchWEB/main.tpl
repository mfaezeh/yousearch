<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="it">
<head>
<title>YouSearch - Clustered search 4 YouTube</title>
<script type="text/javascript" src="/YouSearch/js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="/YouSearch/js/jquery.progressbar.min.js"></script>
<style>
.progressBar{
	display : none;
}
</style>
</head>
<body>
<div align="center">
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="center">&nbsp;.:: TITOLO ::.</td>
		</tr>
		<tr>
			<td align="left">keyword: <b>KEYWORD</b></td>
		</tr>
		<tr>
			<td>
					<div id="search-box">
						<script type="text/javascript">
							$(document).ready(function() {
							var timerId;
							var progressBarValue = 0;
							$("#searchbar").progressBar(0,{ barImage: '/YouSearch/images/progressbg_green.gif'} );
							$.get("http://localhost:8080/YouSearch/search/KEYWORD",{},
											function(data){
													clearInterval(timerId);
													$('#result-box').html(data).hide();
													$("#searchbar").progressBar(100);
													$("#searchbar").hide();
													$('#result-box').show();
										});
									timerId = setInterval(function(){$("#searchbar").progressBar((progressBarValue++)%100)},500);
									
							});							
						</script>
					</div>
			</td>
		</tr>
		<tr>
			<td>
					<div id="result-box">	
						<span class="progressbar" id="searchbar">0%</span>			
					</div>
			</td>
		</tr>
		<tr>
			<td>.:: COPYRIGHT ::.</td>
		</tr>
	</table>
</div>
</body>
</html>
