$(document).ready(function(){
	$('.user-profile').click(function() {
		if(!$(this).hasClass('active')){
			$('.user-profile.active').removeClass('active');
			$(this).addClass('active');
		}
	});
});
