/*
 *  Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function() {
  'use strict';
  var module = angular.module('bk.evaluateJobManager', ['bk.utils', 'bk.evaluatorManager', 'firebase']);

  module.factory('bkEvaluateJobManager', function(bkUtils, bkEvaluatorManager, $firebase, bkSessionManager) {

    var setOutputCellText = function(cell, text) {
      if (!cell.output) {
        cell.output = {};
      }
      cell.output.result = text;
    };
    var _promise = bkUtils.newPromise();
    var _theEvaluator = null;
    var _evaluate = function(cell) {
      if (!cell.evaluator) {
        return;
      }
      var lastPromise = _promise;
      setOutputCellText(cell, "pending");
      var evaluateCell = function() {
        var evaluator = bkEvaluatorManager.getEvaluator(cell.evaluator);
        if (evaluator) {
          var evalP = lastPromise.then(function() {

            bkSessionManager.getEvaluations().$add({
              input: cell.input.body,
              evaluator: evaluator.pluginName,
              output: {
                result: "pending"
              }
            }).then(function(ref) {
              return ref.name(); // evalId
            }).then(function(evalId) {
              var sessionId = bkSessionManager.getSessionId();
              console.log("SessionID = ", bkSessionManager.getSessionId());
              console.log("eval ID = ", evalId);
//              var outputRef = new Firebase(window.fb.ROOT_URL + "_evaluations/" + evalId + "/output");
//              outputRef.on("value", function(snapshot) {
//                console.log("AAA", snapshot.val());
//                cell.output = snapshot.val();
//              });
              var out = new Firebase(window.fb.ROOT_URL + sessionId + "/_evaluations/" + evalId + "/output");
              cell.output = $firebase(out);
              _theEvaluator = evaluator;
              bkUtils.log("evaluate", {
                plugin: evaluator.pluginName,
                length: cell.input.body.length});
              return _theEvaluator.evaluate(cell.input.body, cell.output, evalId, sessionId);
            });
          });
          evalP.catch(function(ret) {
            if (ret === "cancelled by user") {
              _promise = bkUtils.newPromise();
            }
            if (cell.output && cell.output.result === "pending") {
              cell.output.result = "";
            }
          });
          evalP.finally(function() {
            _theEvaluator = null;
          });
          return evalP;
        } else {
          setOutputCellText(cell, "waiting for evaluator initialization ...");
          return bkUtils.delay(500).then(function() {
            return evaluateCell();
          });
        }
      };
      _promise = evaluateCell();
      return _promise;
    };

    return {
      evaluate: function(cell) {
        return _evaluate(cell);
      },
      evaluateAll: function(cells) {
        _(cells).each(_evaluate);
        return _promise;
      },
      isCancellable: function() {
        return !!(_theEvaluator && _theEvaluator.cancelExecution);
      },
      cancel: function() {
        if (_theEvaluator) {
          if (_theEvaluator.cancelExecution) {
            _theEvaluator.cancelExecution();
          } else {
            throw "cancel is not supported for the current evaluator";
          }
        }
      },
      isAnyInProgress: function() {
        return !!_theEvaluator;
      }
    };
  });

})();
