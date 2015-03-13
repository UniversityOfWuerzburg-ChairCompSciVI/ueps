/** ------------------------------------------------------------ */
var block = false;
var synctime = true;
var edit_url = "";

function setEditUrl(url) {
  this.edit_url = url;
  jQuery( ".exercise_class" ).dblclick(function() {
    window.location.href = url;
  });
}

function hide_message_timeout(){
  setTimeout(function() { jQuery('.ui-messages').fadeOut(500); }, 10000);
}

jQuery(document).ready(function() {
  // http://manos.malihu.gr/jquery-custom-content-scroller
  jQuery(".question-text-scrollpanel").mCustomScrollbar({theme:"minimal-dark", scrollInertia:500, autoExpandScrollbar:true });

  jQuery("select").blur();

  var delay = parseInt(jQuery("[id*=sessionTime]").html());

  function countdown() {
    delay = parseInt(jQuery("[id*=sessionTime]").html());
    setTimeout(countdown, 1000);

    delay--;
    if (delay < 0) {
      delay = 0;
    } else {
      jQuery('[id*=sessionTime]').html(delay);
    }
  }
  countdown();

  jQuery(function() {
    // jQuery('.dialog-fix').css("min-width", "800px");
    // jQuery('.dialog-fix').css("min-height", "270px");
    // jQuery('.dialog-fix').css("top", "69px !important");
    // jQuery('.er_diagram_popup').css('top', '50px', 'important');
    // jQuery('.dialog-fix2').css("width", "500px");
    // dialog.jq.css("left",Math.max(0, ((jQuery(window).width() - jQuery(dialog).outerWidth()) / 2) + jQuery(window).scrollLeft()) + "px");
  });


});

function closeAllDialog() {
   for (var propertyName in PrimeFaces.widgets) {
     if (PrimeFaces.widgets[propertyName] instanceof PrimeFaces.widget.Dialog ||
         PrimeFaces.widgets[propertyName] instanceof PrimeFaces.widget.LightBox) {
         PrimeFaces.widgets[propertyName].hide();
     }
   }
}


function formatDialog(className, user_result, solution_dialog, er_diagram) {
  // add custom scrollbar
  jQuery(".ui-dialog-content").mCustomScrollbar(
    {theme:"minimal-dark", scrollInertia:500, axis:"yx", autoExpandScrollbar:"true"});

  var dialogClassName = "." + className;
  var dialogContentClassName = dialogClassName + ' .ui-dialog-content';

  // ------------------------------------------------ //
  // -- Calculate width
  // ------------------------------------------------ //
  var widthOffset = 45;
  var defaultWidth = 850;
  var width = 0;
  if (er_diagram){
    width = jQuery(dialogClassName + " img").width() + widthOffset;
  } else {
    width = jQuery(dialogClassName + " .ui-datatable").width() + widthOffset;
  }
  if (solution_dialog) {
    width = Math.max(jQuery(dialogClassName + " .solution_table").width(), 
                     jQuery(dialogClassName + " .solution_carousel").width()) + widthOffset;
  }
  var windowWidth = jQuery(window).width();
  var minWidth = 100;
  var maxWidth = windowWidth - 100;

  if (width < minWidth + 100) {
    width = minWidth + 100;
  }

  if (width < maxWidth) {
    maxWidth = width;
  }

  if (width < defaultWidth) {
    defaultWidth = width;
  }

  jQuery(dialogClassName).css("min-width", minWidth + "px");
  jQuery(dialogClassName + ' .mCSB_container').css("min-width", minWidth + "px");
  jQuery(dialogClassName + ' .mCSB_container').css("margin","auto");
  jQuery(dialogClassName + ' .ui-paginator').css("min-width","240px");
  jQuery(dialogContentClassName).css("max-width", maxWidth + "px", "important");
  jQuery(dialogContentClassName).css("min-width", (minWidth + 15) + "px");
  jQuery(dialogContentClassName).css("width", defaultWidth + "px", 'important');
  // jQuery(dialogClassName).css("width", defaultWidth + "px", 'important');
  jQuery(dialogClassName).css("max-width", maxWidth + "px", "important");

  // ------------------------------------------------ //
  // -- Calculate height 
  // ------------------------------------------------ //
  var heightOffset = 105;
  var defaultHeight = 450;
  var height = 0; 
  if (er_diagram){
    height = jQuery(dialogClassName + " img").height() + heightOffset + 20;
  } else {
    height = jQuery(dialogClassName + " .ui-datatable").height() + heightOffset; 
  }
  if (solution_dialog) {
    height = jQuery(dialogClassName + " .solution_table").height() 
             + jQuery(dialogClassName + " .solution_carousel").height() + heightOffset - 30;
  }
  var windowHeight = jQuery(window).height();
  var minHeight = 100;
  var maxHeight = windowHeight - 100;


  if (height < minHeight) {
    height = minHeight;
  }
  if (height < maxHeight) {
    maxHeight = height;
    if (solution_dialog) {
      maxHeight += 100;
    }
  }

  if (height < defaultHeight) {
    defaultHeight = height;
  }

  jQuery(dialogClassName).css("min-height", minHeight + "px");
  jQuery(dialogClassName + ' .mCSB_container').css("min-height", minHeight + "px");
  jQuery(dialogContentClassName).css("max-height", (maxHeight - 32) + "px", "important");
  jQuery(dialogContentClassName).css("min-height", (minHeight + 15) + "px");
  jQuery(dialogContentClassName).css("height", defaultHeight + "px", 'important');
  jQuery(dialogClassName).css("height", (defaultHeight + 37) + "px", 'important');
  jQuery(dialogClassName).css("max-height", maxHeight + "px", "important");

  // ------------------------------------------------ //
  // -- Positioning
  // ------------------------------------------------ //

  var randomNumberLeft = Math.floor(Math.random() * 41) - 20;
  var randomNumberTop = Math.floor(Math.random() * 41) - 20;

  var pageOffsetLeft = jQuery('#content').offset().left;
  var pageOffsetTop = jQuery('#content').offset().top;

  jQuery(dialogClassName).css("top", (pageOffsetTop + 30 + randomNumberTop) + "px", "important");

  if (user_result || solution_dialog) {
    jQuery(dialogClassName).css("left", (pageOffsetLeft + 200 + randomNumberLeft) + "px", "important");
  }
  else {
    jQuery(dialogClassName).css("left", (pageOffsetLeft + 30 + randomNumberLeft) + "px", "important");
  }


  // ------------------------------------------------ //
  // -- 
  // ------------------------------------------------ //

  // jQuery('.' + className).resize(function() {
    // var prevHeight = jQuery('.' + className).height();
    // alert(jQuery('.' + className).offset().left);
    // jQuery('.' + className).css("left", "613px", "important");
    // jQuery('.' + className).offset({ left: 613 })
  // });
}

jQuery.ajaxSetup({
  error : handleXhrError,
  success : handleSuccess
});

function handleXhrError(xhr) {
  startAjaxStatus();
  setTimeout("ajax_error_dialog.show()", 3000);
}

function handleSuccess(xhr) {
  if (!block) {
    blockUpdate();
    syncSessionDisplay();
    setTimeout("unblockUpdate()", 2000);
  }
}

function syncSessionDisplay() {
  if (synctime) {
    lazyload();
    lazyload_online();
  }
}

function blockUpdate() {
  block = true;
}

function unblockUpdate() {
  block = false;
}

function startAjaxStatus() {
  jQuery("#ajaxStatusPanel_prestart").css({
    display : "block"
  });
  setTimeout("endAjaxStatus()", 1500);
}

function endAjaxStatus() {
  jQuery("#ajaxStatusPanel_prestart").css({
    display : "none"
  });

  jQuery("#ajaxStatusPanel_default").css({
    display : "none"
  });
}

function collapseIntroduction() {
  startAjaxStatus();
  intro.unselect(0);
}

function editLastDatatableRow() {
  jQuery('.ui-datatable-tablewrapper tr').last().find('span.ui-icon-pencil')
      .each(function() {
        jQuery(this).click()
      });
}

jQuery(document).ready(function() {

  if (detectIE()) {
    jQuery("#shitty_internet_explorer").css({
      display : "block"
    });
  }

  jQuery('.tree_description').pulsate({
     glow:   false,
     reach:  10,
     color:  '#DCE0FF',
     repeat: 3
  });

  jQuery('.admin_help_text').pulsate({
     glow:   false,
     reach:  10,
     color:  '#FFE5E5',
     repeat: 2
  });

//  jQuery('.unrated_tab').click(function() {
//    startAjaxStatus();
//    setTimeout("startAjaxStatus()", 500);
//  });
//  jQuery('.rated_tab').click(function() {
//    startAjaxStatus();
//    setTimeout("startAjaxStatus()", 500);
//  });

  jQuery( ".tree_doubleclick" ).dblclick(function() {
    // this.parentNode.parentNode.firstChild.click();
  });

  jQuery( ".tree_doubleclick" ).click(function() {
    // jQuery("#form\\:exerciseTree\\:0_0 .tree_doubleclick").trigger({
    //   type: 'mousedown',
    //   which: 3
    // }).trigger({
    //   type: 'mouseup',
    //   which: 3
    // });

      // tableMenu.show();
      // alert ('fuck');
  });
});


function pulsateButton() {
  jQuery('#usertask\\:user_result_button').pulsate({
     glow:   false,
     reach:  10,
     color: '#D3D8FF',
     repeat: 2
  });

  jQuery('#usertask\\:result_button').pulsate({
     glow:   false,
     reach:  10,
     color: '#D3D8FF',
     repeat: 2
  });

  jQuery('#usertask\\:feedback_accordion').pulsate({
     glow:   false,
     reach:  10,
     color: '#D3D8FF',
     repeat: 2
  });

  jQuery('#saved_query_box').pulsate({
     glow:   false,
     reach:  10,
     color: '#D3D8FF',
     repeat: 2
  });
}

var siteFunctions = {
  // patch to fix a problem that the context menu disappears after update
  // delay the show to occure after the update
  patchContextMenuShow : function() {
    'use strict';
    var protShow = PrimeFaces.widget.ContextMenu.prototype.show;
    siteFunctions.patchContextMenuShow.lastEvent = null;
    PrimeFaces.widget.ContextMenu.prototype.show = function(e) {
      var ret;
      if (e) {
        siteFunctions.patchContextMenuShow.lastEvent = e;
        siteFunctions.patchContextMenuShow.lastEventArg = arguments;
        siteFunctions.patchContextMenuShow.lastEventContext = this;
      } else if (siteFunctions.patchContextMenuShow.lastEvent) {
        ret = protShow.apply(
            siteFunctions.patchContextMenuShow.lastEventContext,
            siteFunctions.patchContextMenuShow.lastEventArg);
        siteFunctions.patchContextMenuShow.lastEvent = null;
      }
      return ret;
    };
  }
};

jQuery(document).ready(function() {
  'use strict';
  try {
    siteFunctions.patchContextMenuShow();
  } catch (e) {
    console.error(e);
  }
});

function expand_tree() {
  var treeExcludingRoot = jQuery(".ui-treenode-children").first();
  jQuery(".ui-tree-toggler.ui-icon-triangle-1-e", treeExcludingRoot).click();
}

function collapse_tree() {
  var treeExcludingRoot = jQuery(".ui-treenode-children").first();
  jQuery(".ui-tree-toggler.ui-icon-triangle-1-s", treeExcludingRoot).click();
}


// jQuery("#gif_image").waitUntilExists(function() {
// });

// jQuery(document).ready(function(){
// jQuery('#gif_image').each(function(e){
// var src = jQuery(e).attr('src');
// alert(src);
// jQuery(e).hover(function(){
// jQuery(this).attr('src', src.replace('_dea.gif', '.gif'));
// }, function(){
// jQuery(this).attr('src', src);
// });
// });
// });

jQuery(document).ready(function() {
  jQuery("#gif_image").hover(function() {
    jQuery(this).attr("src", "/sql/resources/img/help.gif");
  }, function() {
    jQuery(this).attr("src", "/sql/resources/img/help_dea.gif");
  });
});




PrimeFaces.locales['de'] = {
    closeText: 'Schließen',
    prevText: 'Zurück',
    nextText: 'Weiter',
    monthNames: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],
    monthNamesShort: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
    dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'],
    dayNamesShort: ['Son', 'Mon', 'Die', 'Mit', 'Don', 'Fre', 'Sam'],
    dayNamesMin: ['S', 'M', 'D', 'M ', 'D', 'F ', 'S'],
    weekHeader: 'Woche',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Nur Zeit',
    timeText: 'Zeit',
    hourText: 'Stunde',
    minuteText: 'Minute',
    secondText: 'Sekunde',
    currentText: 'Aktuelles Datum',
    ampm: false,
    month: 'Monat',
    week: 'Woche',
    day: 'Tag',
    allDayText: 'Ganzer Tag'
};

function detectIE() {
    var ua = window.navigator.userAgent;
    var msie = ua.indexOf('MSIE ');
    var trident = ua.indexOf('Trident/');

    if (msie > 0) {
        // IE 10 or older => return version number
        // return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    return true;
    }

    if (trident > 0) {
        // IE 11 (or newer) => return version number
        var rv = ua.indexOf('rv:');
        //return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    return true;
    }

    // other browser
    return false;
}

function hideIEMessage() {
  jQuery("#shitty_internet_explorer").css({
    display : "none"
  });
}

// var siteFunctions = {
//     //patch to fix a problem that the context menu disappears after update
//     //delay the show to occure after the update
//     patchContextMenuShow: function() {
//         'use strict';
//         var protShow = PrimeFaces.widget.ContextMenu.prototype.show;
//         siteFunctions.patchContextMenuShow.lastEvent = null;
//         PrimeFaces.widget.ContextMenu.prototype.show = function(e) {
//             var ret;
//             if (e) {
// //                console.log('saving last event');
//                 siteFunctions.patchContextMenuShow.lastEvent = e;
//                 siteFunctions.patchContextMenuShow.lastEventArg = arguments;
//                 siteFunctions.patchContextMenuShow.lastEventContext = this;
//             } else if (siteFunctions.patchContextMenuShow.lastEvent) {
// //                console.log('executing last event');
//                 ret = protShow.apply(siteFunctions.patchContextMenuShow.lastEventContext, siteFunctions.patchContextMenuShow.lastEventArg);
// //                console.log('clearing last event');
//                 siteFunctions.patchContextMenuShow.lastEvent = null;
//             }
//             return ret;
//         };
//     }
// };

// jQuery(document).ready(function() {
//     'use strict';
//     try {
//         siteFunctions.patchContextMenuShow();
//     } catch (e) {
//         console.error(e);
//     }
// });

// var currentEvent;
// jQuery(document).ready(function() {
//   PrimeFaces.widget.ContextMenu.prototype.show = function(e) {
//      //hide other contextmenus if any
//      jQuery(document.body).children('.ui-contextmenu:visible').hide();

//      if(e) {
//         currentEvent = e;
//      }

//      var win = jQuery(window),
//      left = e.pageX,
//      top = e.pageY,
//      width = this.jq.outerWidth(),
//      height = this.jq.outerHeight();

//      //collision detection for window boundaries
//      if((left + width) > (win.width())+ win.scrollLeft()) {
//         left = left - width;
//      }
//      if((top + height ) > (win.height() + win.scrollTop())) {
//         top = top - height;
//      }

//      if(this.cfg.beforeShow) {
//         this.cfg.beforeShow.call(this);
//      }

//      this.jq.css({
//         'left': left,
//         'top': top,
//         'z-index': ++PrimeFaces.zindex
//      }).show();

//      e.preventDefault();
//   };
// });
