var exports,console,$true,$false,$finished,process$,$empty;//IGNORE
function Comparison(x){}//IGNORE
function ArraySequence(x){}//IGNORE
function Entry(a,b){}//IGNORE
function Singleton(x){}//IGNORE
function SequenceBuilder(){}//IGNORE
function Integer(x){}//IGNORE
function Boolean$(f){}//IGNORE
function String$(f,x){}//IGNORE

function print(line) { process$.writeLine(line.getString()); }
exports.print=print;

var larger = Comparison("larger");
function getLarger() { return larger }
var smaller = Comparison("smaller");
function getSmaller() { return smaller }
var equal = Comparison("equal");
function getEqual() { return equal }
function largest(x, y) { return x.compare(y) === larger ? x : y }
function smallest(x, y) { return x.compare(y) === smaller ? x : y }

exports.getLarger=getLarger;
exports.getSmaller=getSmaller;
exports.getEqual=getEqual;
exports.largest=largest;
exports.smallest=smallest;

//receives ArraySequence, returns element
function min(seq) {
    var v = seq.value[0];
    if (seq.value.length > 1) {
        for (var i = 1; i < seq.value.length; i++) {
            v = smallest(v, seq.value[i]);
        }
    }
    return v;
}
//receives ArraySequence, returns element 
function max(seq) {
    var v = seq.value[0];
    if (seq.value.length > 1) {
        for (var i = 1; i < seq.value.length; i++) {
            v = largest(v, seq.value[i]);
        }
    }
    return v;
}
//receives ArraySequence of ArraySequences, returns flat ArraySequence
function join(seqs) {
    if (seqs === undefined) return $empty;
    var builder = [];
    var i1 = seqs.getIterator();
    var seq; while ((seq = i1.next()) !== $finished) {
        var i2 = seq.getIterator();
        var e; while ((e = i2.next()) !== $finished) {
            builder.push(e);
        }
    }
    return ArraySequence(builder);
}
//receives ArraySequences, returns ArraySequence
function zip(keys, items) {
    var entries = []
    var numEntries = Math.min(keys.value.length, items.value.length);
    for (var i = 0; i < numEntries; i++) {
        entries[i] = Entry(keys.value[i], items.value[i]);
    }
    return ArraySequence(entries);
}
//receives and returns ArraySequence
function coalesce(seq) {
    if (seq === undefined) return $empty;
    var newseq = [];
    for (var i = 0; i < seq.value.length; i++) {
        if (seq.value[i]) {
            newseq.push(seq.value[i]);
        }
    }
    return ArraySequence(newseq);
}

//receives ArraySequence and CeylonObject, returns new ArraySequence
function append(seq, elem) {
    return ArraySequence(seq.value.concat(elem));
}
function prepend(seq, elem) {
    if (seq.getEmpty() === $true) {
        return Singleton(elem);
    } else {
        var sb = SequenceBuilder();
        sb.append(elem);
        sb.appendAll(seq);
        return sb.getSequence();
    }
}

//Receives Iterable, returns ArraySequence (with Entries)
function entries(seq) {
    if (seq === undefined) return $empty;
    var e = [];
    var iter = seq.getIterator();
    var i = 0;
    var elem; while ((elem = iter.next()) !== $finished) {
        e.push(Entry(Integer(i++), elem));
    }
    return ArraySequence(e);
}

function any(/*Boolean...*/ values) {
    if (values === undefined) return $false;
    var it = values.getIterator();
    var v;
    while ((v = it.next()) !== $finished) {
        if (v === $true) {return $true;}
    }
    return $false;
}
function every(/*Boolean...*/ values) {
    if (values === undefined) return $false;
    var it = values.getIterator();
    var v;
    while ((v = it.next()) !== $finished) {
        if (v === $false) {return $false;}
    }
    return $true;
}

function first(/*Element...*/ elements) {
    if (elements === undefined) return null;
    var e = elements.getIterator().next();
    return (e !== $finished) ? e : null;
}

exports.min=min;
exports.max=max;
exports.join=join;
exports.zip=zip;
exports.coalesce=coalesce;
exports.append=append;
exports.prepend=prepend;
exports.entries=entries;
exports.any=any;
exports.every=every;
exports.first=first;

//These are operators for handling nulls
function exists(value) {
    return value === null || value === undefined ? $false : $true;
}
function nonempty(value) {
    return value === null || value === undefined ? $false : Boolean$(value.getEmpty() === $false);
}

function isOfType(obj, typeName) {
    if (obj === null) {
        return Boolean$(typeName==="ceylon.language.Nothing" || typeName==="ceylon.language.Void");
    }
    if (typeof obj === 'function') {
        return Boolean$(typeName === 'ceylon.language.Callable');
    }
    return Boolean$(obj.getT$all && typeName in obj.getT$all());
}
function isOfTypes(obj, types) {
    if (obj===null) {
        return types.l.indexOf('ceylon.language.Nothing')>=0 || types.l.indexOf('ceylon.language.Void')>=0;
    }
    if (typeof obj === 'function') {
        return Boolean$(types.l.indexOf('ceylon.language.Callable')>=0);
    }
    var unions = false;
    var inters = true;
    var _ints=false;
    var objTypes = obj.getT$all();
    for (var i = 0; i < types.l.length; i++) {
        var t = types.l[i];
        var partial = false;
        if (typeof t === 'string') {
            partial = t in objTypes;
        } else {
            partial = isOfTypes(obj, t);
        }
        if (types.t==='u') {
            unions = partial || unions;
        } else {
            inters = partial && inters;
            _ints=true;
        }
    }
    return _ints ? inters||unions : unions;
}

function className(obj) {
    if (obj === null) return String$('ceylon.language.Nothing');
    return String$(obj.getT$name());
}

function identityHash(obj) {
    return obj.identifiableObjectID;
}

//This is just so that you can pass a comprehension and return it as iterable
function elements(iter) {
    return iter;
}
exports.elements=elements;
exports.exists=exists;
exports.nonempty=nonempty;
exports.isOfType=isOfType;
exports.isOfTypes=isOfTypes;
exports.className=className;
exports.identityHash=identityHash;
