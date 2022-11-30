filetype plugin indent on
syntax on

" set permanent line numbering
set number relativenumber

" we want to know exact position
set ruler

" control tabwidth to be 2 tabs wide
set expandtab
set shiftwidth=2
set softtabstop=2

" keep indentation level
set autoindent

" map two semicolons to escape key
:inoremap ;; <Esc>

" let new window splits happen below
set splitbelow

" command for terminal creation below all splits
:command Bterm botright terminal
