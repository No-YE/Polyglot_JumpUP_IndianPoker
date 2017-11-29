require('date-utils');

var keepChip=0;

var socketRoom= {};
var room={};

module.exports = function (io) {
    io.on('connection', function (socket) {
        var rooms = io.sockets.adapter.rooms;

        console.log('connect');

        socket.on('requestGameMatch', function(){   
        
            console.log('requestGameMatch');

            for (var key in room){
                if(key == socket.id) continue;

                if (key == ''){
                    continue;
                }

                var realRoom = JSON.stringify(rooms[key]);
                realRoom = JSON.parse(realRoom);
                realRoom = realRoom.length;
            
                if (realRoom == 1){
                    console.log('clientNum is one');
                    socket.join(key);
                    io.sockets.in(key).emit('completeMatch');
                    
                    //socketRoom에 플레이어 정보도 같이 넣는다.
                    socketRoom[socket.id] = {
                        "playRoom": key,  
                        "orderingCard": undefined, 
                        "nChip": 20, 
                        "cardNum": 1, 
                        "nBetChip": 1,
                        "turn": undefined,
                        "oth_id": undefined //상대방 socket.id
                    }; 
                    return;
                }
            }
            console.log('made room');
            socket.join(socket.id);
            room[socket.id]=socket.id;
            socketRoom[socket.id] = {
                "playRoom": socket.id, 
                "orderingCard": undefined, 
                "nChip": 20, 
                "cardNum": 1, 
                "nBetChip": 1,
                "turn": undefined,
                "oth_id": undefined //상대방 socket.id
            };
        });

        socket.on('cancelGameMatch', function(data){
            socket.leave(socketRoom[socket.id].playRoom);
            delete room[socket.id];
        });

        socket.on('orderingCardSelect', function(orderingCard) {
            socketRoom[socket.id].orderingCard = orderingCard;
            for(var otherId in io.sockets.adapter.rooms[socketRoom[socket.id].playRoom].sockets) { //방의 모든 클라이언트 돌려서 내가 아니면 상대로 간주, oth_id에 넣는다.
                console.log(otherId);
                if(otherId != socket.id) {  
                    socketRoom[socket.id].oth_id=otherId; 
                }
            }
            console.log('orderingCard: ',socketRoom[socket.id].orderingCard);
            if(socketRoom[socketRoom[socket.id].oth_id].orderingCard != undefined && socketRoom[socket.id].orderingCard != undefined){
                if(socketRoom[socketRoom[socket.id].oth_id].orderingCard>socketRoom[socket.id].orderingCard) {
                    socket.emit('NoticeOrder', false);
                    socket.broadcast.to(socketRoom[socket.id].playRoom).emit('NoticeOrder', true); 
                    socketRoom[socket.id].turn=false;
                    socketRoom[socketRoom[socket.id].oth_id].turn=true;
                }
                else if(socketRoom[socketRoom[socket.id].oth_id].orderingCard==socketRoom[socket.id].orderingCard) {
                    console.log('same same');
                    socketRoom[socket.id].orderingCard = undefined;
                    socketRoom[socketRoom[socket.id].oth_id].orderingCard = undefined;
                    io.sockets.in(socketRoom[socket.id]).emit('reCardSelect'); //만약 같으면 다시 뽑기 이벤트 전송
                } else {
                    console.log('you are big');
                    socket.emit('NoticeOrder', true);
                    socket.broadcast.to(socketRoom[socket.id].playRoom).emit('NoticeOrder', false);
                    socketRoom[socket.id].turn=true;
                    socketRoom[socketRoom[socket.id].oth_id].turn=false;
                } 
    
                setTimeout(function() {
                    socketRoom[socket.id].cardNum = Math.floor(Math.random() * 10) + 1;
                    socketRoom[socket.id].nBetChip = 1;
                    socketRoom[socketRoom[socket.id].oth_id].nBetChip = 1;
                    socketRoom[socket.id].nChip -= 1;
                    socketRoom[socketRoom[socket.id].oth_id].nChip -= 1;
                    socket.emit('InitialPlayerInfo', socketRoom[socket.id].nChip
                                                   , socketRoom[socket.id].nBetChip
                                                   , socketRoom[socketRoom[socket.id].oth_id].cardNum
                                                   , socketRoom[socketRoom[socket.id].oth_id].nChip
                                                   , socketRoom[socketRoom[socket.id].oth_id].nBetChip); //나에게 나와 상대 정보 전송

                    socket.broadcast.to(socketRoom[socket.id].playRoom).emit('InitialPlayerInfo', socketRoom[socketRoom[socket.id].oth_id].nChip
                                                           , socketRoom[socketRoom[socket.id].oth_id].nBetChip
                                                           , socketRoom[socket.id].cardNum
                                                           , socketRoom[socket.id].nChip
                                                           , socketRoom[socket.id].nBetChip);
                }, 1000);   
            }
        });

        socket.on('feeling', function(data) {
            //감정 표현
            socket.broadcast.to(socketRoom[socket.id].playRoom).emit('otherFeeling', data);
        });

        socket.on('EndTurn', function(nBetChip) {

            console.log(nBetChip);
            if (chkEndBetting(socket.id, nBetChip, function () {
                console.log('끝나버림');
                socket.emit('BettingResult', socketRoom[socket.id].cardNum
                                           , socketRoom[socket.id].nChip
                                           , socketRoom[socket.id].nBetChip
                                           , socketRoom[socketRoom[socket.id].oth_id].nChip
                                           , socketRoom[socketRoom[socket.id].oth_id].nBetChip); //나에게 나와 상대 정보 전송

                socket.broadcast.to(socketRoom[socket.id].playRoom).emit('BettingResult', socketRoom[socketRoom[socket.id].oth_id].cardNum
                                                                                        , socketRoom[socketRoom[socket.id].oth_id].nChip
                                                                                        , socketRoom[socketRoom[socket.id].oth_id].nBetChip
                                                                                        , socketRoom[socket.id].nChip
                                                                                        , socketRoom[socket.id].nBetChip);
 
                if (socketRoom[socket.id].nChip === 0 || socketRoom[socket.id].nChip === undefined) {
                    console.log(socket.id + ' 패배');
                    delete room[socket.id];
                } else if (socketRoom[socketRoom[socket.id].oth_id].nChip === 0 || socketRoom[socketRoom[socket.id].oth_id].nChip === undefined) {
                    console.log(socketRoom[socket.id].oth_id + ' 패배');
                    delete room[socket.id];
                }
                else {
                    setTimeout(function () {
                        startSetting(function () {
                            socket.emit('InitialPlayerInfo', socketRoom[socket.id].nChip
                                                           , socketRoom[socket.id].nBetChip
                                                           , socketRoom[socketRoom[socket.id].oth_id].cardNum
                                                           , socketRoom[socketRoom[socket.id].oth_id].nChip
                                                           , socketRoom[socketRoom[socket.id].oth_id].nBetChip);

                            socket.broadcast.to(socketRoom[socket.id].playRoom).emit('InitialPlayerInfo'
                                                           , socketRoom[socketRoom[socket.id].oth_id].nChip
                                                           , socketRoom[socketRoom[socket.id].oth_id].nBetChip
                                                           , socketRoom[socket.id].cardNum
                                                           , socketRoom[socket.id].nChip
                                                           , socketRoom[socket.id].nBetChip);
                        });
                    }, 5000);
                }
            })) return;
            console.log('턴 종료'); //상대 턴 종료
            socket.broadcast.to(socketRoom[socket.id].playRoom).emit('TurnChanging', socketRoom[socket.id].nChip
                                          , socketRoom[socket.id].nBetChip
                                          , socketRoom[socketRoom[socket.id].oth_id].nChip
                                          , socketRoom[socketRoom[socket.id].oth_id].nBetChip); //이것도 마찬가지ㅎㅎ
            socketRoom[socket.id].turn = !socketRoom[socket.id].turn;
            socketRoom[socketRoom[socket.id].oth_id].turn = !socketRoom[socketRoom[socket.id].oth_id].turn;
        });

        socket.on('disconnect', function () {
            console.log('disconnect!!');
            if(socketRoom[socket.id]!=null) {
                socket.broadcast.to(socketRoom[socket.id].playRoom).emit('otherGo');

                socket.leave(socketRoom[socket.id].playRoom);
            /*socketRoom[socket.id] = {
                "playRoom": undefined, 
                "orderingCard": undefined, 
                "nChingCard": undefined, 
                "cardNumip": 28, 
                "nBetChip": undefined,
                "turn": undefined,
                "oth_id": undefined //상대방 socket.id
            };*/
                delete room[socket.id];
            }
        });

        var chkEndBetting = function (socketId, nBetChip, callback) {
                console.log(socket.id+'의 턴');
                if (nBetChip === -1) {    //포기하였을 때
                    console.log('포기함');
                    if (socketRoom[socket.id].cardNum === 10) {                                                                           //포기했는데 카드가 10이네
                        console.log('cardNum 이 10임');
                        socketRoom[socketRoom[socket.id].oth_id].nChip += socketRoom[socket.id].nBetChip + socketRoom[socketRoom[socket.id].oth_id].nBetChip + keepChip + 10;         //패널티
                        socketRoom[socket.id].nChip -= 10;
                        keepChip = 0;
                        callback();
                        return true;
                    } else {
                        console.log('cardNum 이 10이 아님');
                        socketRoom[socketRoom[socket.id].oth_id].nChip += socketRoom[socket.id].nBetChip + socketRoom[socketRoom[socket.id].oth_id].nBetChip + keepChip;
                        keepChip = 0;
                        callback();
                        return true;
                    }
                }
        
                else {
                    console.log('포기하지 않음');
                    socketRoom[socket.id].nBetChip += nBetChip;
                    socketRoom[socket.id].nChip -= nBetChip;
                    console.log(socketRoom[socket.id].nBetChip + " / " + socketRoom[socket.id].nChip);
        
                    if (socketRoom[socket.id].nBetChip === socketRoom[socketRoom[socket.id].oth_id].nBetChip) {
                        console.log('배팅한 칩의 개수가 같음');
                        if (socketRoom[socket.id].cardNum > socketRoom[socketRoom[socket.id].oth_id].cardNum) {
                            console.log('player1의 카드숫자가 더 높음');
                            socketRoom[socket.id].nChip += socketRoom[socket.id].nBetChip + socketRoom[socketRoom[socket.id].oth_id].nBetChip + keepChip;
                            keepChip = 0;
                            callback();
                            return true;
                        } else if (socketRoom[socket.id].cardNum < socketRoom[socketRoom[socket.id].oth_id].cardNum) {
                            console.log('player2의 카드숫자가 더 높음');
                            socketRoom[socketRoom[socket.id].oth_id].nChip += socketRoom[socket.id].nBetChip + socketRoom[socketRoom[socket.id].oth_id].nBetChip + keepChip;
                            keepChip = 0;
                            callback();
                            return true;
                        } else if (socketRoom[socket.id].cardNum === socketRoom[socketRoom[socket.id].oth_id].cardNum) {
                            console.log('두 플레이어의 카드 숫자가 같음');
                            keepChip += socketRoom[socket.id].nBetChip + socketRoom[socketRoom[socket.id].oth_id].nBetChip + keepChip;
                            callback();
                            return true;
                        }
                    }
                }
                return false;
            }
        
            
            

        var startSetting = function (callback) {
            console.log('스타트세팅');
            socketRoom[socket.id].cardNum = Math.floor(Math.random() * 10) + 1;
            socketRoom[socketRoom[socket.id].oth_id].cardNum = Math.floor(Math.random() * 10) + 1;
            socketRoom[socket.id].nBetChip = 1;
            socketRoom[socket.id].nChip -= 1;
            socketRoom[socketRoom[socket.id].oth_id].nBetChip = 1;
            socketRoom[socketRoom[socket.id].oth_id].nChip -= 1;
            callback();
        };
    });
};


//player1 == socketRoom[socket.id]
//player2 == socketRoom[socketRoom[socket.id].oth_id]