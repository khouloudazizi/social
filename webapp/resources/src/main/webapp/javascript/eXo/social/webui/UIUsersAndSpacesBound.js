(function($) {
  var bound = {
    build: function(selector, url, placeholder) {
      $('#' + selector).suggester({
        type: 'tag',
        placeholder: placeholder,
        plugins: ['remove_button', 'restore_on_backspace'],
        create: false,
        createOnBlur: false,
        highlight: false,
        openOnFocus: false,
        sourceProviders: ['exo:social'],
        valueField: 'value',
        labelField: 'text',
        searchField: ['text'],
        closeAfterSelect: true,
        dropdownParent: 'body',
        hideSelected: true,
        renderMenuItem: function(item, escape) {
          return '<div class="item">' + escape(item.value) + '</div>';
        },
        renderItem: function(item) {
          return '<div class="item">' + item.text + '</div>';
        },
        onItemRemove: function(item) {
          this.$input[0].selectize.removeOption(item);
          this.$input[0].selectize.removeItem(item);
        },
        sortField: [{field: 'order'}, {field: '$score'}],
        providers: {
          'exo:social': function(query, callback) {
            if (query === '') {
              var thizz = this;
              // Pre-load options for initial users
              if (this.items && this.items.length > 0) {
                $.ajax({
                  type: "GET",
                  url: url,
                  data: { nameToSearch : this.items.join() + "," },
                  complete: function(jqXHR) {
                    if(jqXHR.readyState === 4) {
                      var json = $.parseJSON(jqXHR.responseText);
                      if (json.options != null) {
                        callback(json.options);
                        for (var i = 0; i < json.options.length; i++) {
                          thizz.updateOption(json.options[i].value, json.options[i]);
                        }
                      }
                    }
                  }
                });
              }
            } else {
              $.ajax({
                type: "GET",
                url: url,
                data: { nameToSearch : query },
                complete: function(jqXHR) {
                  if(jqXHR.readyState === 4) {
                    var json = $.parseJSON(jqXHR.responseText);
                    if (json.options != null) {
                      callback(json.options);
                    }
                  }
                }
              });
            }
          }
        }
      });
    }
  };

  return bound;
})($);