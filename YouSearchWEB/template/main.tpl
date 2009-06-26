<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="it">
<head>
<title>YouSearch - Clustered search 4 YouTube</title>
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/jquery.progressbar.min.js"></script>
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
			<td align="left">keyword: <b>{KEYWORD}</b></td>
		</tr>
		<tr>
			<td>
					<div id="search-box">
					<span class="progressbar" id="searchbar">0%</span>
						<script type="text/javascript">
							$(document).ready(function() {
								$("#searchbar").progressBar(80);
							});							
						</script>
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
