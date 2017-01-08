(function($) {
    var invite = {
        build: function(selector, url, placeholder) {
            $('#' + selector).suggester({
                type : 'tag',
                placeholder: placeholder,
                plugins: ['remove_button', 'restore_on_backspace'],
                maxItems: null,
                valueField: 'value',
                labelField: 'text',
                searchField: ['text'],
                sourceProviders: ['exo:social_space_member'],
                create: function(input) {
                    return {'value': input, 'text': input, 'invalid': true};
                },
                createOnBlur: true,
                renderItem: function(item, escape) {
                    if (item.invalid) {
                        return '<div class="item invalid">' + item.text + '</div>';
                    } else {
                        return '<div class="item">' + item.text + '</div>';                         
                    }
                },
                renderMenuItem: function(item, escape) {
                  var avatar = item.avatar;
                  if (avatar == null) {
                      if (item.type == "space") {
                          avatar = '/eXoSkin/skin/images/system/SpaceAvtDefault.png';
                      } else {
                          avatar = '/eXoSkin/skin/images/system/UserAvtDefault.png';
                      }
                  }

                  return '<div class="option">' +
                  '<img width="20px" height="20px" src="' + avatar + '"> ' +
                  escape(item.text) + '</div>';
                },
                providers: {
                 'exo:social_space_member': function(query, callback) {
                    if (query && query.trim() != '') {
                        $.ajax({
                            type: "GET",
                            url: url,
                            data: { nameToSearch : query },
                            complete: function(jqXHR) {
                                if(jqXHR.readyState === 4) {
                                    var data = $.parseJSON(jqXHR.responseText)
                                    if (data && data.length > 0) {
                                        callback(data);
                                    }
                                }
                            }
                        });
                    }
                  } 
                }
            });            
        },

        notify: function(selector, anchor) {
            $(anchor).append($(selector));
            setTimeout(function(){ $(anchor).fadeOut() }, 5000);
        }
    };

    return invite;
})($);