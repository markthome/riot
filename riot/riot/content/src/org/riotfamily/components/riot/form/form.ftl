<?xml version="1.0" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<#if customStyleSheets??>
			<#list customStyleSheets as item>
				<@riot.stylesheet href=item />
			</#list>
		</#if>
		<@riot.stylesheet href="style/common.css" />
		<@riot.stylesheet href="style/form.css" />
		<@riot.stylesheet href="style/form-custom.css" />
		<@riot.stylesheet href="style/component-form.css" />
		<@riot.stylesheet href="style/component-form-custom.css" />
		<@riot.script src="prototype/prototype.js" />
		<@riot.script src="riot/window/dialog.js" />
	</head>
	<body>
		${html}
		<script>
			if (parent != window && parent.riot) {
				var dlg = parent.riot.window.getDialog(window);
				dlg.setSize(${form.dimension.width + 60}, ${form.dimension.height + 100});
			}
		</script>
	</body>
</html>